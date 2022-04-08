package commands;

import connection_control.ConnectionController;
import connection_control.Request;
import data_control.DataController;
import exceptions.IncorrectArgumentException;
import exceptions.UnknownCommandException;

import java.io.IOException;
import java.util.ArrayList;
/*
TODO: проверить терминальность методов потоков в командах
TODO: проверка enum'ом в виде аргументов
TODO: execute_script
TODO: проверить ВСЕ КОМАНДЫ
TODO: проверить на ошибки соединения
TODO: javadoc
 */
/**
 * controls execution of all commands
 */
public class CommandController {
    /**
     * max value of commands for keep in history
     */
    protected static final int MAX_COMMANDS_IN_HISTORY = 13;
    /**
     * that controls data for program
     */
    private final DataController dataController;
    private final ConnectionController connectionController = new ConnectionController();
    /**
     * history of all commands that was used
     */
    private final ArrayList<Command> history;
    /**
     * collection of all commands that user can use
     */
    private final ArrayList<Command> allCommands;
    private final ArrayList<CommandInfo> allCommandsInfo;
    public CommandController (final DataController dataController) {
        this.dataController = dataController;
        history = new ArrayList<>();
        allCommands = new ArrayList<>();
        allCommandsInfo = new ArrayList<>();
        commandInit();
        try {
            connectionController.start();
        } catch (IOException e) {
            System.out.println("Не удалось развернуть сервер. Попробуйте развернуть его на другом порте.");
            e.printStackTrace();
            return;
        }
        startWorkWithClient();
    }

    private void startWorkWithClient() {
        System.out.println("Ожидание подключение клиента...");
        history.clear();
        try {
            connectionController.connect();
            connectionController.sendObject(allCommandsInfo);
        } catch (IOException e) {
            System.out.println("Ошибка попытки соединения с клиентом.");
        }
        listenRequests();
    }

    /**
     * Initialization commands to allCommands that can be used by user
     */
    private void commandInit() {
        allCommands.add(new HelpCommand());
        allCommands.add(new InfoCommand());
        allCommands.add(new ShowCommand());
        allCommands.add(new InsertCommand());
        allCommands.add(new UpdateCommand());
        allCommands.add(new RemoveKeyCommand());
        allCommands.add(new ClearCommand());
        allCommands.add(new SaveCommand());
        allCommands.add(new ExitCommand());
        allCommands.add(new ExecuteScriptCommand());
        allCommands.add(new HistoryCommand());
        allCommands.add(new ReplaceIfGreaterCommand());
        allCommands.add(new RemoveLowerKeyCommand());
        allCommands.add(new FilterGreaterThanClimateCommand());
        allCommands.add(new PrintAscendingCommand());
        allCommands.add(new PrintFieldAscendingGovernment());
        allCommands.forEach(command -> {
            if (!command.isServerCommand())
                allCommandsInfo.add(new CommandInfo(command.getName(), command.getSendInfo(),command.getArgInfo()));
        });
    }

    public void listenRequests() {
        String[] input;
        Request request;
        Command command;
        while(connectionController.isConnected()) {
            try {
                request = processRequest();
                if (!request.getRequestCode().equals(Request.RequestCode.COMMAND)) {
                    System.out.println("Получен некорректный запрос от клиента.");
                    continue;
                }
                input = request.getMsg().split(" ");
            } catch (IOException e) {
                System.out.println("Ошибка получения запроса");
                break;
            } catch (ClassNotFoundException e) {
                System.out.println("Получен некорректный запрос от клиента.");
                continue;
            }
            try {
                try {
                    command = searchCommand(input[0].toLowerCase());
                    invoke(command, input);
                } catch (IncorrectArgumentException e) {
                    System.out.println("Некорректный аргумент: " + e.getMessage());
                    sendError("получен некорректный аргумент команды - "+e.getMessage());
                } catch (UnknownCommandException e) {
                    System.out.println("Получена команда, неизвестная серверу.");
                    sendError("получена неизвестная серверу команда");
                } catch (ClassNotFoundException e) {
                    System.out.println("Получены неопознанные данные от клиента");
                    sendError("получены неопознанные данные от клиента");
                }
            } catch (IOException e) {
                break;
            }
        }
        if (connectionController.isConnected())
            System.out.println("Ошибка подключения с клиентом. Сброс соединения...");
        startWorkWithClient();
    }
    public void sendOK() throws IOException {
        connectionController.sendObject(new Request(Request.RequestCode.OK,""));
    }
    public void sendReply(String msg) throws IOException {
        connectionController.sendObject(new Request(Request.RequestCode.REPLY, msg));
    }
    public void sendError(String msg) throws IOException {
        connectionController.sendObject(new Request(Request.RequestCode.ERROR,msg+"\n"));
    }
    private Request processRequest() throws IOException, ClassNotFoundException {
        return (Request)connectionController.receiveObject();
    }
    /**
     * Execute command and add it in history
     * @param command that need to invoke
     * @param args for this command
     * @throws IncorrectArgumentException if requiring args is incorrect
     */
    protected void invoke(final Command command,final String[] args) throws IncorrectArgumentException, IOException, ClassNotFoundException {
        System.out.println("Получена команда "+command.getName());
        String reply = command.execute(this, args);
        if (reply != null) {
            System.out.println("Отправлен ответ.");
            sendReply(reply);
        }
        addToHistory(command);
    }

    /**
     * Add command to history and if history is overflow delete first command
     * @param command that be added to history
     */
    private void addToHistory (Command command) {
        if(history.size() == MAX_COMMANDS_IN_HISTORY) {
            history.remove(0);
        }
        history.add(command);
    }
    protected ArrayList<Command> getHistory() {
        return history;
    }

    /**
     * Parse string to command
     * @param name of command
     * @return command
     * @throws UnknownCommandException if name of command doesn't equal with name in command's constructor
     */
    protected Command searchCommand (final String name) throws UnknownCommandException {
        for(Command i: allCommands) {
            if(i.getName().equals(name))
                return i;
        }
        throw new UnknownCommandException();
    }
    protected DataController getDataController() {
        return dataController;
    }

    public ConnectionController getConnectionController() {
        return connectionController;
    }

    public ArrayList<Command> getAllCommands() {
        return allCommands;
    }
}
