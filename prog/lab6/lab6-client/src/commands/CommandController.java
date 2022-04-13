package commands;

import connection_control.ConnectionController;
import connection_control.Request;
import data_classes.Climate;
import data_classes.Government;
import data_control.ConsoleController;
import data_control.FileController;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * controls execution of all commands
 */
public class CommandController {
    private final ConsoleController consoleController = new ConsoleController(this);
    private ConnectionController connectionController;
    private final FileController fileController = new FileController(this);
    private ArrayList<CommandInfo> allCommandsInfo;
    private final Scanner scanner;
    public CommandController () {
        this.scanner = new Scanner(System.in);
        try {
            connectionController = new ConnectionController(this);
        } catch (MissingArgumentException e) {
            System.out.println("Ошибка в файле конфигурации: "+e.getMessage());
            return;
        } catch (FileNotFoundException e) {
            System.out.println("Не найден файл конфигурации. Создайте файл конфигурации config.excalibbur\n" +
                    "И добавьте в него строки \"address: localhost\" (допускается обычный ip, сервера) и \"port: 1234\" (порт сервера)");
            return;
        }
        connect();
    }

    private void connect () {
        try {
            connectionController.reopenChannel();
        } catch (IOException e) {
            System.out.println("Ошибка открытия сетевого канала.");
            return;
        }
        System.out.println("Попытка подключения к "+connectionController.getAddress()+":"+connectionController.getPort()+"...");
        while(!connectionController.tryToConnect()) {
            System.out.println("Ошибка подключения к серверу. Попробовать снова? (y/n)");
            if (!scanner.nextLine().toLowerCase().equals("y")) {
                exit();
            }
            try {
                connectionController.reopenChannel();
            } catch (IOException e) {
                System.out.println("Ошибка открытия сетевого канала.");
                return;
            }
        }
        try {
            allCommandsInfo = (ArrayList<CommandInfo>) connectionController.processConnection();
        } catch (IOException e) {
            System.out.println("Ошибка получения данных конфигурации сервера.");
            return;
        } catch (ClassNotFoundException e) {
            System.out.println("Получены неопознанные данные от сервера.");
            return;
        }
        System.out.println("Успешное соединение с сервером.");
        listenConsole();
    }

    private void listenConsole() {
        String input = "";
        String[] args;
        CommandInfo command;
        while (true) {
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
                    connectionController.sendObject(connectionController.getChannel(), new Request(Request.RequestCode.COMMAND, input));
                } catch (IOException e) {
                    System.out.println("Не удалось отправить команду на сервер.");
                    break;
                }
                try {
                    if (invoke(command, args))
                        processRequest(connectionController.receiveRequest());
                } catch (IOException e) {
                    System.out.println("Ошибка получения запроса от сервера");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    System.out.println("Получен некорректный ответ от сервера.");
                }
            }
        }
        System.out.println("Соединение с сервером было разорвано.\nХотите попробовать переподключиться? (y/n)");
        if (scanner.nextLine().equals("y"))
            connect();
        else
            exit();
    }
    public boolean invoke(CommandInfo command, String[] args) throws IOException, ClassNotFoundException {
        if (command.getSendInfo() == null)
            return true;
        Request request;
        switch(command.getSendInfo()) {
            // могут слать null!!!
            case CITY:
                request = connectionController.receiveRequest();
                if(!request.getRequestCode().equals(Request.RequestCode.OK)) {
                    processRequest(request);
                    return false;
                }
                System.out.println("Данные id корректны. Продолжение ввода...");
                connectionController.sendObject(connectionController.getChannel(), consoleController.createCityByUser(false));
                break;
            case CITY_UPDATE:
                request = connectionController.receiveRequest();
                if(!request.getRequestCode().equals(Request.RequestCode.OK)) {
                    processRequest(request);
                    return false;
                }
                connectionController.sendObject(connectionController.getChannel(), consoleController.createCityByUser(true));
                break;
            case EXIT:
                exit();
                break;
            case COMMANDS:
                try {
                    Request validRequest = connectionController.receiveRequest();
                    if (!validRequest.getRequestCode().equals(Request.RequestCode.OK)) {
                        processRequest(validRequest);
                        break;
                    }
                    ArrayList<CommandInfo> commandsInfo = fileController.readScriptFile(args[1]);
                    ArrayList<String> strCommand = fileController.getStrCommand();
                    for (int i=0; i<commandsInfo.size();i++) {
                        connectionController.sendObject(connectionController.getChannel(),
                                new Request(Request.RequestCode.COMMAND, strCommand.get(i)));
                        invoke(commandsInfo.get(i),strCommand.get(i).split(" "));
                        processRequest(connectionController.receiveRequest());
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Файл скрипта не найден.");
                }
                finally {
                    connectionController.sendObject(connectionController.getChannel(), new Request(Request.RequestCode.OK,""));
                }
                break;
        }
        return true;
    }
    private void exit() {
        System.out.println("Завершение выполнения программы...");
        try {
            getConnectionController().disconnect();
        } catch (IOException e) {

        }
        System.exit(0);
    }
    public void processRequest (Request request) {
        switch (request.getRequestCode()) {
            case REPLY:
                System.out.print(request.getMsg());
                break;
            case ERROR:
                System.out.print("Ошибка запроса: "+request.getMsg());
                break;
            case OK:
                break;
            default:
                System.out.println(request.getRequestCode()+": "+request.getMsg());
        }
    }
    public boolean isValidCommand (String[] args) {
        CommandInfo command = parseCommand(args[0]);
        if (command == null) {
            System.out.println("Неизвестная команда "+args[0]+", используйте help для вывода списка команд.");
            return false;
        }
        try {
            if (command.getArgInfo() == null) return true;
            if (command.getArgInfo().length > args.length-1)
                throw new MissingArgumentException("недостаточное количество аргументов. Используйте help для справки.");
            else if (command.getArgInfo().length < args.length-1)
                throw new MissingArgumentException("слишком много аргументов. Используйте help для справки.");
            for (int i=0;i<command.getArgInfo().length;i++) {
                if (command.getArgInfo()[i] == null)
                    continue;
                switch (command.getArgInfo()[i]) {
                    case ID:
                        CommandInfo.idValidator(args[i+1]);
                        break;
                    case INT:
                        try {
                            Integer.parseInt(args[i+1]);
                        } catch (NumberFormatException e) {
                            throw new IncorrectArgumentException("аргумент - целое число.");
                        }
                        break;
                    case FLOAT:
                        try {
                            Float.parseFloat(args[i+1]);
                        } catch (NumberFormatException e) {
                            throw new IncorrectArgumentException("аргумент - число с плавающей точкой.");
                        }
                        break;
                    case CLIMATE:
                        if (args[i+1].equals(" "))
                            break;
                        Climate tempClimate = null;
                        for (Climate climate: Climate.values()) {
                            if (args[i+1].toUpperCase().equals(climate.name())) {
                                tempClimate = climate;
                                break;
                            }
                        }
                        if (tempClimate == null) {
                            throw new IncorrectArgumentException("получено некорректное значение climate");
                        }
                        break;
                    case GOVERNMENT:
                        if (args[i+1].equals(" "))
                            break;
                        Government tempGovernment = null;
                        for (Government government: Government.values()) {
                            if (args[i+1].toUpperCase().equals(government.name())) {
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
            System.out.println("Отсутствуют обязательные аргументы: "+e.getMessage());

        } catch (IncorrectArgumentException e) {
            System.out.println("Некорректный аргумент: "+e.getMessage());
        }
        return false;
    }
    public CommandInfo parseCommand (String name) {
        for (CommandInfo command: allCommandsInfo) {
            if (command.getName().equals(name.toLowerCase())) {
                return command;
            }
        }
        return null;
    }

    public ConnectionController getConnectionController() {
        return connectionController;
    }
}
