package commands;

import connection_control.ConnectionController;
import connection_control.Request;
import data_control.DataController;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;
import exceptions.UnknownCommandException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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

    /**
     * that controls connection with user
     */
    private ConnectionController connectionController;

    /**
     * history of all commands that was used
     */
    private final ArrayList<Command> history;

    /**
     * collection of all commands that user can use
     */
    private final ArrayList<Command> allCommands;

    /**
     * collection of data about all commands that will send to user
     */
    private final ArrayList<CommandInfo> allCommandsInfo;

    /**
     * instance for log server's info
     */
    private Logger logger;

    /**
     * Create program working class
     *
     * @param path of file with collection
     * @throws IOException if program can't open file with collection
     */
    public CommandController(String path) throws IOException {
        createLogger();
        this.dataController = new DataController(path, this);
        history = new ArrayList<>();
        allCommands = new ArrayList<>();
        allCommandsInfo = new ArrayList<>();
        try {
            connectionController = new ConnectionController(this);
        } catch (MissingArgumentException e) {
            logger.log(Level.WARNING, "Ошибка файла конфигурации: " + e.getMessage());
            return;
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "Не был найден файл конфигурации config.excalibbur. Добавьте его, указав порт следующим образом:\n" +
                    "port: 1234");
            return;
        }
        commandInit();
    }

    /**
     * Start work of program: turn on connection controller, create logger and receive connection
     */
    public void start() {
        createLogger();
        try {
            connectionController.start();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Не удалось развернуть сервер. Попробуйте развернуть его на другом порте.");
            return;
        }
        processClient();
    }

    /**
     * Create logger with config from file logger.config
     */
    private void createLogger() {
        try (FileInputStream ins = new FileInputStream("logger.config")) {
            LogManager.getLogManager().readConfiguration(ins);
        } catch (FileNotFoundException e) {
            System.out.println("Файл конфигурации логгера не найден.");
        } catch (IOException e) {
            System.out.println("Не удалось открыть файл конфигурации логгера.");
        }
        logger = Logger.getLogger(CommandController.class.getName());
    }

    /**
     * Wait creating connection from user
     */
    private void processClient() {
        logger.log(Level.INFO, "Ожидание подключение клиента...");
        history.clear();
        try {
            connectionController.connect();
            connectionController.sendObject(allCommandsInfo);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка попытки соединения с клиентом.");
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
                allCommandsInfo.add(new CommandInfo(command.getName(), command.getSendInfo(), command.getArgInfo()));
        });
    }

    /**
     * Use when connection with user exists. Listen request from user and execute command from one.
     */
    public void listenRequests() {
        String[] input;
        Request request;
        Command command;
        while (connectionController.isConnected()) {
            try {
                request = receiveRequest();
                if (!request.getRequestCode().equals(Request.RequestCode.COMMAND)) {
                    logger.log(Level.WARNING, "Получен некорректный запрос от клиента.");
                    continue;
                }
                input = request.getMsg().split(" ");
            } catch (IOException e) {
                logger.log(Level.WARNING, "Ошибка получения запроса");
                break;
            } catch (ClassNotFoundException e) {
                logger.log(Level.WARNING, "Получен некорректный запрос от клиента.");
                continue;
            }
            try {
                try {
                    command = searchCommand(input[0].toLowerCase());
                    invoke(command, input);
                } catch (IncorrectArgumentException e) {
                    logger.log(Level.WARNING, "Некорректный аргумент: " + e.getMessage());
                    sendError("получен некорректный аргумент команды - " + e.getMessage());
                } catch (UnknownCommandException e) {
                    logger.log(Level.WARNING, "Получена команда, неизвестная серверу.");
                    sendError("получена неизвестная серверу команда");
                } catch (ClassNotFoundException e) {
                    logger.log(Level.WARNING, "Получены неопознанные данные от клиента");
                    sendError("получены неопознанные данные от клиента");
                }
            } catch (IOException e) {
                break;
            }
        }
        if (connectionController.isConnected())
            logger.log(Level.WARNING, "Ошибка подключения с клиентом. Сброс соединения...");
        processClient();
    }

    /**
     * Send OK request to user
     *
     * @throws IOException if server couldn't send this request
     */
    public void sendOK() throws IOException {
        connectionController.sendObject(new Request(Request.RequestCode.OK, ""));
    }

    /**
     * Send REPLY request to user
     *
     * @param msg that user can see as result of command execution
     * @throws IOException if server couldn't send this request
     */
    public void sendReply(String msg) throws IOException {
        connectionController.sendObject(new Request(Request.RequestCode.REPLY, msg));
    }

    /**
     * Send ERROR request
     *
     * @param msg that user can see as explanation of error
     * @throws IOException if server couldn't send this request
     */
    public void sendError(String msg) throws IOException {
        connectionController.sendObject(new Request(Request.RequestCode.ERROR, msg + "\n"));
    }

    /**
     * Receive request from user
     *
     * @return request
     * @throws IOException            if server couldn't receive this request
     * @throws ClassNotFoundException if server received not expected class
     */
    public Request receiveRequest() throws IOException, ClassNotFoundException {
        return (Request) connectionController.receiveObject();
    }

    /**
     * Execute command and add it in history
     *
     * @param command that need to invoke
     * @param args    for this command
     * @throws IncorrectArgumentException if requiring args is incorrect
     */
    protected void invoke(final Command command, final String[] args) throws IncorrectArgumentException, IOException, ClassNotFoundException {
        logger.log(Level.INFO, "Получена команда " + command.getName());
        addToHistory(command);
        String reply = command.execute(this, args);
        if (reply != null) {
            logger.log(Level.INFO, "Отправлен ответ клиенту.");
            sendReply(reply);
        }
    }

    /**
     * Add command to history and if history is overflow delete first command
     *
     * @param command that be added to history
     */
    private void addToHistory(Command command) {
        if (history.size() == MAX_COMMANDS_IN_HISTORY) {
            history.remove(0);
        }
        history.add(command);
    }

    protected ArrayList<Command> getHistory() {
        return history;
    }

    /**
     * Parse string to command
     *
     * @param name of command
     * @return command
     * @throws UnknownCommandException if name of command doesn't equal with name in command's constructor
     */
    protected Command searchCommand(final String name) throws UnknownCommandException {
        for (Command i : allCommands) {
            if (i.getName().equals(name))
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

    public Logger getLogger() {
        return logger;
    }
}
