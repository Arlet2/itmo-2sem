package commands;

import connection_control.ConnectionController;
import connection_control.Request;
import data_control.ConsoleController;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * controls execution of all commands
 */
public class CommandController {
    private final ConsoleController consoleController = new ConsoleController(this);
    private final ConnectionController connectionController = new ConnectionController(this);
    private ArrayList<CommandInfo> allCommandsInfo;
    public CommandController () {
        Scanner scanner = new Scanner(System.in);
        connect(scanner);
    }

    private void connect (Scanner scanner) {
        try {
            connectionController.reopenChannel();
        } catch (IOException e) {
            System.out.println("Ошибка открытия сетевого канала.");
            return;
        }
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
        listenConsole(scanner);
    }

    public void listenConsole(Scanner scanner) {
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
                    if (invoke(command))
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
            connect(scanner);
        else
            exit();
    }
    public boolean invoke(CommandInfo command) throws IOException, ClassNotFoundException {
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
            default:
                System.out.println(request.getRequestCode()+": "+request.getMsg());
        }
    }
    private boolean isValidCommand (String[] args) {
        CommandInfo command = parseCommand(args[0]);
        if (command == null) {
            System.out.println("Неизвестная команда, используйте help для вывода списка команд.");
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
    private CommandInfo parseCommand (String name) {
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

    public ArrayList<CommandInfo> getAllCommandsInfo() {
        return allCommandsInfo;
    }
}
