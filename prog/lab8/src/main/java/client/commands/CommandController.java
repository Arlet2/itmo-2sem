package client.commands;

import client.data_control.ConsoleController;
import client.data_control.DataController;
import client.data_control.FileController;
import client.connection_control.ConnectionController;
import server.commands.Command;
import connect_utils.DataTransferObject;
import connect_utils.Serializer;
import data_classes.City;
import data_classes.Climate;
import data_classes.Government;
import exceptions.ConfigFileNotFoundException;
import exceptions.ConnectionException;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * controls execution of all commands
 */
public class CommandController {
    /**
     * Constant of max value in history
     */
    public static final int MAX_COMMANDS_IN_HISTORY = 13;

    /**
     * controls user's interaction with console
     */
    private final ConsoleController consoleController = new ConsoleController();

    /**
     * controls connection with server
     */
    private final ConnectionController connectionController;

    /**
     * controls reading from file
     */
    private final FileController fileController = new FileController(this);

    private final DataController dataController = new DataController(this);

    /**
     * All info about commands that can send to server and execute
     */
    private ArrayList<Command> allCommands;

    private ArrayList<Command> history;

    private volatile boolean isConnected;

    /**
     * Create scanner and read configuration for connection
     * After that start connection with user
     */
    public CommandController() throws MissingArgumentException, ConfigFileNotFoundException, ConnectionException {
        connectionController = new ConnectionController(this);
    }

    /**
     * connect to server
     */
    public void connect() throws ConnectionException {
        Scanner scanner = new Scanner(System.in);
        try {
            connectionController.openChannel();
        } catch (IOException e) {
            throw new ConnectionException("Ошибка открытия сетевого канала.");
        }
        System.out.println("Попытка подключения к " + connectionController.getAddress().getHostName() +
                ":" + connectionController.getAddress().getPort() + "...");
        while (!connectionController.tryToConnect()) {
            System.out.println("Ошибка подключения к серверу. Попробовать снова? (y/n)");
            if (!scanner.nextLine().equalsIgnoreCase("y")) {
                exit();
            }
            try {
                connectionController.openChannel();
            } catch (IOException e) {
                throw new ConnectionException("Ошибка открытия сетевого канала.");
            }
        }
        try {
            allCommands = new ArrayList<>(connectionController.getRequestController().getCommands());
            dataController.updateMap(connectionController.getRequestController().getCities());
        } catch (IOException e) {
            throw new ConnectionException("Ошибка получения данных конфигурации сервера.");
        } catch (ClassNotFoundException e) {
            throw new ConnectionException("Получены неопознанные данные от сервера.");
        }
        System.out.println("Успешное соединение с сервером.");
        System.out.println("Авторизуйтесь для работы. Используйте help для помощи.");
        history = new ArrayList<>();
        isConnected = true;
        new Thread(this::listenConsole).start();
        new Thread(() -> {
            while (isConnected) {
                try {
                    processRequest();
                } catch (IOException e) {
                    System.out.println("Ошибка соединения с сервером");
                    isConnected = false;
                } catch (ClassNotFoundException e) {
                    System.out.println("Получен неизвестный запрос от сервера");
                }
            }
        }).start();
    }

    /**
     * Listen console for new command that sends it to server if it's correct
     */
    private void listenConsole() {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        String[] args;
        Command command;
        while (isConnected) {
            System.out.print("$ ");
            try {
                input = scanner.nextLine().replaceAll(" +", " ");
            } catch (NoSuchElementException e) {
                System.out.println("Гений.");
                exit();
            }
            args = input.split(" ");
            if (isValidCommand(args)) { // проверить арги и имя команды
                command = parseCommand(args[0]);
                try {
                    invoke(command, input);
                } catch (IOException e) {
                    System.out.println("Ошибка получения запроса от сервера");
                    isConnected = false;
                } catch (ClassNotFoundException e) {
                    System.out.println("Получен некорректный ответ от сервера.");
                }
            }
        }
        System.out.println("Соединение с сервером было разорвано.\nХотите попробовать переподключиться? (y/n)");
        if (scanner.nextLine().equals("y")) {
            try {
                connect();
            } catch (ConnectionException e) {
                System.out.println(e.getMessage());
            }
        } else
            exit();
    }

    /**
     * Send extra info to server (if it needs)
     *
     * @param command that need to execute
     * @param input   as args of this command
     * @return <b>true</b> if invoke is successfully done; <b>false</b> if execution has got troubles
     * @throws IOException            if request couldn't receive or send
     * @throws ClassNotFoundException if request couldn't deserialize
     */
    public void invoke(Command command, String input) throws IOException, ClassNotFoundException {
        addCommandToHistory(command);
        connectionController.getRequestController().sendRequest(
                new DataTransferObject(DataTransferObject.Code.COMMAND, input));
        if (command.getSendInfo() == null)
            return;
        switch (command.getSendInfo()) {
            case CITY:
                connectionController.getRequestController().sendCity(
                        consoleController.createCityByUser(false));
                break;
            case CITY_UPDATE:
                connectionController.getRequestController().sendCity(consoleController.createCityByUser(true));
                break;
            case COMMANDS:
                break;
        }
    }

    private void addCommandToHistory(Command command) {
        if (history.size() == MAX_COMMANDS_IN_HISTORY) {
            history.remove(0);
        }
        history.add(command);
    }

    /**
     * End program execution
     */
    private void exit() {
        System.out.println("Завершение выполнения программы...");
        try {
            getConnectionController().disconnect();
        } catch (IOException ignored) {

        }
        System.exit(0);
    }

    /**
     * Print information using request code
     */
    public void processRequest() throws IOException, ClassNotFoundException {
        DataTransferObject dataTransferObject = connectionController.getRequestController().receiveRequest();
        switch (dataTransferObject.getCode()) {
            case REPLY:
                System.out.print(dataTransferObject.getMsg());
                break;
            case ERROR:
                System.out.print("Ошибка запроса: " + dataTransferObject.getMsg());
                break;
            case OK:
                System.out.println("ОК");
                break;
            case NOT_REQUEST:
                switch (dataTransferObject.getDataType()) {
                    case CITIES_ARRAY:
                        System.out.println("ОБНОВЛЕНИЕ!");
                        dataController.updateMap(
                                (Collection<City>) Serializer.convertBytesToObject(dataTransferObject.getDataBytes()));
                        break;
                    case COMMANDS_ARRAY:
                        System.out.println("ДЕРЬМО!");
                        break;
                }
                break;
            default:
                System.out.println("Получен неожиданный ответ от сервера: "
                        + dataTransferObject.getCode() + ": " + dataTransferObject.getMsg());
        }
    }

    /**
     * Check command's args before sending to server
     *
     * @param args of command
     * @return <b>true</b> if args is correct else <b>false</b>
     */
    public boolean isValidCommand(String[] args) {
        Command command = parseCommand(args[0]);
        if (command == null) {
            System.out.println("Неизвестная команда " + args[0] + ", используйте help для вывода списка команд.");
            return false;
        }
        try {
            if (command.getArgInfo() == null) return true;
            if (command.getArgInfo().length > args.length - 1)
                throw new MissingArgumentException("недостаточное количество аргументов. " +
                        "Используйте help для справки.");
            else if (command.getArgInfo().length < args.length - 1)
                throw new MissingArgumentException("слишком много аргументов. Используйте help для справки.");
            for (int i = 0; i < command.getArgInfo().length; i++) {
                if (command.getArgInfo()[i] == null)
                    continue;
                switch (command.getArgInfo()[i]) {
                    case ID:
                        City.idValidator(args[i + 1]);
                        if (command.getSendInfo() == Command.SendInfo.CITY) {
                            if (!dataController.isUniqueId(Long.parseLong(args[i + 1]))) {
                                System.out.println("Такой id уже есть.");
                                return false;
                            }
                        } else if (command.getSendInfo() == Command.SendInfo.CITY_UPDATE) {
                            if (dataController.isUniqueId(Long.parseLong(args[i + 1]))) {
                                System.out.println("Такого id нет");
                                return false;
                            }
                        }
                        break;
                    case INT:
                        try {
                            Integer.parseInt(args[i + 1]);
                        } catch (NumberFormatException e) {
                            throw new IncorrectArgumentException("аргумент - целое число.");
                        }
                        break;
                    case FLOAT:
                        try {
                            Float.parseFloat(args[i + 1]);
                        } catch (NumberFormatException e) {
                            throw new IncorrectArgumentException("аргумент - число с плавающей точкой.");
                        }
                        break;
                    case CLIMATE:
                        if (args[i + 1].equals(" "))
                            break;
                        Climate tempClimate = null;
                        for (Climate climate : Climate.values()) {
                            if (args[i + 1].toUpperCase().equals(climate.name())) {
                                tempClimate = climate;
                                break;
                            }
                        }
                        if (tempClimate == null) {
                            throw new IncorrectArgumentException("получено некорректное значение climate");
                        }
                        break;
                    case GOVERNMENT:
                        if (args[i + 1].equals(" "))
                            break;
                        Government tempGovernment = null;
                        for (Government government : Government.values()) {
                            if (args[i + 1].toUpperCase().equals(government.name())) {
                                tempGovernment = government;
                                break;
                            }
                        }
                        if (tempGovernment == null) {
                            throw new IncorrectArgumentException("получено некорректное значение government");
                        }
                        break;
                    case STRING:
                        break;
                }
            }
            return true;
        } catch (MissingArgumentException e) {
            System.out.println("Отсутствуют обязательные аргументы: " + e.getMessage());

        } catch (IncorrectArgumentException e) {
            System.out.println("Некорректный аргумент: " + e.getMessage());
        }
        return false;
    }

    /**
     * Search and return CommandInfo by name
     *
     * @param name of command
     * @return CommandInfo
     */
    public Command parseCommand(String name) {
        for (Command command : allCommands) {
            if (command.getName().equals(name.toLowerCase())) {
                return command;
            }
        }
        return null;
    }

    public ConnectionController getConnectionController() {
        return connectionController;
    }

    public FileController getFileController() {
        return fileController;
    }

    public DataController getDataController() {
        return dataController;
    }
}
