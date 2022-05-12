package server.commands;

import exceptions.ConfigFileNotFoundException;
import server.Logger;
import server.connection_control.ConnectionController;
import connect_utils.*;
import server.connection_control.User;
import server.data_control.DataController;
import exceptions.IncorrectArgumentException;
import exceptions.MissingArgumentException;
import exceptions.UnknownCommandException;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * controls execution of all commands
 */
public class CommandController {

    public static final int MAX_COMMANDS_IN_HISTORY = 13;

    /**
     * that controls data for program
     */
    private final DataController dataController;

    /**
     * that controls connection with user
     */
    private final ConnectionController connectionController;

    /**
     * collection of all commands that user can use
     */
    private final ArrayList<Command> allCommands = new ArrayList<>();

    /**
     * collection of data about all commands that will send to user
     */
    private final ArrayList<CommandInfo> allCommandsInfo = new ArrayList<>();

    /**
     *
     */
    private final ArrayList<Command> authCommands = new ArrayList<>();

    private final HashMap<String, ArrayList<Command>> history = new HashMap<>();

    /**
     * Create program working class
     */
    public CommandController() throws SQLException, MissingArgumentException, ConfigFileNotFoundException {
        this.dataController = new DataController(this);
        connectionController = new ConnectionController(this);
        commandInit();
    }

    /**
     * Start work of program: turn on connection controller and receive connection
     */
    public void start() {
        try {
            connectionController.start();
        } catch (IOException e) {
            Logger.getLogger().log(Level.WARNING, "Не удалось развернуть сервер. " +
                    "Попробуйте развернуть его на другом порте.");
            return;
        }
        processClient();
    }

    /**
     * Wait creating connection from user
     */
    private void processClient() {
        //Logger.getLogger().log(Level.INFO, "Ожидание подключение клиента...");
        Socket socket;
        try {
            socket = connectionController.connect();
            connectionController.getRequestController().sendCommandList(allCommandsInfo);
        } catch (IOException e) {
            Logger.getLogger().log(Level.WARNING, "Ошибка попытки соединения с клиентом.");
            return;
        }
        String login = clientAuth();
        if (login == null)
            return;
        history.put(login, new ArrayList<>());
        listenRequests(new User(socket, login));
    }

    /**
     * Initialization commands to allCommands that can be used by user
     */
    private void commandInit() {
        allCommands.add(new HelpCommand());
        allCommands.add(new RegisterCommand());
        allCommands.add(new LoginCommand());
        allCommands.add(new InfoCommand());
        allCommands.add(new ShowCommand());
        allCommands.add(new InsertCommand());
        allCommands.add(new UpdateCommand());
        allCommands.add(new RemoveKeyCommand());
        allCommands.add(new ClearCommand());
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
        authCommands.add(allCommands.get(0));
        authCommands.add(allCommands.get(1));
        authCommands.add(allCommands.get(2));
    }

    private String clientAuth() {
        Request request;
        String[] args;
        while (true) {
            try {
                request = connectionController.getRequestController().receiveRequest();
            } catch (IOException e) {
                Logger.getLogger().log(Level.WARNING, "Ошибка получения запроса");
                break;
            } catch (ClassNotFoundException e) {
                Logger.getLogger().log(Level.WARNING, "Получен некорректный запрос от клиента.");
                continue;
            }
            try {
                try {
                    args = request.getMsg().split(" ");
                    Command command = searchCommand(args[0]);
                    if (authCommands.contains(command))
                        invoke(null, searchCommand(args[0]), args);
                    else {
                        connectionController.getRequestController().sendError("Доступ запрещен " +
                                "неавторизованным пользователям.\nИспользуйте команды login или register " +
                                "для авторизации или регистрации.\n" +
                                "Пример использования: login sadness 1234");
                        continue;
                    }

                } catch (IncorrectArgumentException e) {
                    Logger.getLogger().log(Level.INFO, "Ошибка авторизации: " + e.getMessage());
                    connectionController.getRequestController().sendError(e.getMessage());
                    continue;
                } catch (ClassNotFoundException e) {
                    Logger.getLogger().log(Level.WARNING, "Ошибка получения запроса от клиента");
                    continue;
                }
            } catch (IOException ex) {
                Logger.getLogger().log(Level.WARNING, "Потеряно соединение с клиентом");
                break;
            }
            if (!args[0].equals("help")) {
                return args[1];
            }
        }
        return null;
    }

    /**
     * Use when connection with user exists. Listen request from user and execute command from one.
     */
    private void listenRequests(User user) {
        String[] input;
        Request request;
        Command command;
        while (user.isConnected()) {
            try {
                request = connectionController.getRequestController().receiveRequest();
                if (!request.getRequestCode().equals(Request.RequestCode.COMMAND)) {
                    Logger.getLogger().log(Level.WARNING, "Получен некорректный запрос от клиента.");
                    continue;
                }
                input = request.getMsg().split(" ");
            } catch (IOException e) {
                Logger.getLogger().log(Level.WARNING, "Ошибка получения запроса");
                break;
            } catch (ClassNotFoundException e) {
                Logger.getLogger().log(Level.WARNING, "Получен некорректный запрос от клиента.");
                continue;
            }
            try {
                try {
                    command = searchCommand(input[0].toLowerCase());
                    invoke(user, command, input);
                } catch (IncorrectArgumentException e) {
                    Logger.getLogger().log(Level.WARNING, "Некорректный аргумент: " + e.getMessage());
                    connectionController.getRequestController().sendError("получен некорректный аргумент команды - " + e.getMessage());
                } catch (UnknownCommandException e) {
                    Logger.getLogger().log(Level.WARNING, "Получена команда, неизвестная серверу.");
                    connectionController.getRequestController().sendError("получена неизвестная серверу команда");
                } catch (ClassNotFoundException e) {
                    Logger.getLogger().log(Level.WARNING, "Получены неопознанные данные от клиента");
                    connectionController.getRequestController().sendError("получены неопознанные данные от клиента");
                }
            } catch (IOException e) {
                break;
            }
        }
        if (user.isConnected())
            Logger.getLogger().log(Level.WARNING, "Ошибка подключения с клиентом. Сброс соединения...");
    }

    /**
     * Execute command and add it in history
     *
     * @param command that need to invoke
     * @param args    for this command
     * @throws IncorrectArgumentException if requiring args is incorrect
     */
    protected void invoke(User user, final Command command, final String[] args) throws IncorrectArgumentException, IOException, ClassNotFoundException {
        Logger.getLogger().log(Level.INFO, "Получена команда " + command.getName());
        addCommandToHistory(user.getLogin(), command);
        String reply = command.execute(user, this, args);
        if (reply != null) {
            Logger.getLogger().log(Level.INFO, "Отправлен ответ клиенту.");
            connectionController.getRequestController().sendReply(reply);
        }
    }

    public ArrayList<Command> getHistory(String login) {
        return history.get(login);
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

    public void addCommandToHistory(String login, Command command) {
        if (history.get(login).size() == MAX_COMMANDS_IN_HISTORY) {
            history.get(login).remove(0);
        }
        history.get(login).add(command);
    }

    public DataController getDataController() {
        return dataController;
    }

    public ConnectionController getConnectionController() {
        return connectionController;
    }

    public ArrayList<Command> getAllCommands() {
        return allCommands;
    }

    public ArrayList<Command> getAuthCommands() {
        return authCommands;
    }
}
