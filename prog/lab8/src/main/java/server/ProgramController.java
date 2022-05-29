package server;

import exceptions.ConfigFileNotFoundException;
import server.commands.*;
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
import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.logging.Level;


/**
 * controls execution of all commands
 */
public class ProgramController {

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
    private final ArrayList<Command> allCommands = new ArrayList<>(16);

    private final ExecutorService listeners = Executors.newCachedThreadPool();
    private final ExecutorService executors = Executors.newFixedThreadPool(5);
    private final ForkJoinPool senders = new ForkJoinPool(3);

    private final LinkedList<User> users;

    /**
     * Create program working class
     */
    public ProgramController() throws SQLException, MissingArgumentException, ConfigFileNotFoundException {
        this.dataController = new DataController();
        connectionController = new ConnectionController();
        commandInit();
        users = new LinkedList<>();
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
        listeners.execute(this::processClient);
    }

    /**
     * Wait creating connection from user
     */
    private void processClient() {
        Socket socket;
        User user;
        try {
            socket = connectionController.connect();
            user = new User(socket, null);
            CommandInfo authData = (CommandInfo) Serializer.convertBytesToObject(
                    connectionController.getRequestController()
                            .receiveRequest(user, DataTransferObject.Code.COMMAND).getDataBytes());
            try {
                String[] args = (String[]) Serializer.convertBytesToObject(authData.getArgs());
                String reply;
                if (authData.getName().equals("login"))
                    reply = new LoginCommand().execute(user, this, args);
                else if (authData.getName().equals("register"))
                    reply = new RegisterCommand().execute(user, this, args);
                else
                    throw new IOException();
                senders.execute(() -> {
                    try {
                        connectionController.getRequestController().sendReply(user, reply);
                    } catch (IOException e) {
                        user.disconnect();
                    }
                });
                user.setLogin(args[1]);
            } catch (IncorrectArgumentException e) {
                senders.execute(() -> {
                    try {
                        connectionController.getRequestController().sendError(user, e.getMessage());
                    } catch (IOException ex) {
                        user.disconnect();
                    }
                });
            }
        } catch (IOException | ClassNotFoundException e) {
            Logger.getLogger().log(Level.WARNING, "Ошибка попытки соединения с клиентом.");
            return;
        } finally {
            listeners.execute(this::processClient);
        }
        users.add(user);
        try {
            connectionController.getRequestController().receiveOK(user);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {

            }
            connectionController.getRequestController().sendCollection(user,
                    dataController.getMap());
        } catch (IOException e) {
            user.disconnect();
            return;
        }
        listenRequests(user);
    }

    /**
     * Initialization commands to allCommands that can be used by user
     */
    private void commandInit() {
        allCommands.add(new RegisterCommand());
        allCommands.add(new LoginCommand());
        allCommands.add(new InsertCommand());
        allCommands.add(new UpdateCommand());
        allCommands.add(new RemoveKeyCommand());
        allCommands.add(new ClearCommand());
        allCommands.add(new ReplaceIfGreaterCommand());
        allCommands.add(new RemoveLowerKeyCommand());
    }

    /**
     * Use when connection with user exists. Listen request from user and execute command from one.
     */
    private void listenRequests(User user) {
        if (user.isDisconnected())
            return;
        DataTransferObject dataTransferObject;
        Command command;
        CommandInfo commandInfo;
        try {
            dataTransferObject = connectionController.getRequestController().receiveRequest(user,
                    DataTransferObject.Code.COMMAND);
            if (!dataTransferObject.getCode().equals(DataTransferObject.Code.COMMAND)) {
                Logger.getLogger().log(Level.WARNING, "Получен некорректный запрос от клиента.");
                listeners.execute(() -> listenRequests(user));
            }
        } catch (IOException e) {
            Logger.getLogger().log(Level.WARNING, "Ошибка получения запроса");
            user.disconnect();
            return;
        } catch (ClassNotFoundException e) {
            Logger.getLogger().log(Level.WARNING, "Получен некорректный запрос от клиента.");
            listeners.execute(() -> listenRequests(user));
            return;
        }
        try {
            commandInfo = (CommandInfo) Serializer.convertBytesToObject(dataTransferObject.getDataBytes());
            command = searchCommand(commandInfo.getName());
        } catch (IOException | ClassNotFoundException e) {
            user.disconnect();
            return;
        }
        Future<String> futureReply = executors.submit(() -> {
            try {
                try {
                    return invoke(user, command, Serializer.convertBytesToObject(
                            commandInfo.getArgs()));
                } catch (IncorrectArgumentException e) {
                    Logger.getLogger().log(Level.WARNING, "Некорректный аргумент: " + e.getMessage());
                    senders.execute(() -> {
                        try {
                            connectionController.getRequestController()
                                    .sendError(user, e.getMessage());
                        } catch (IOException ex) {
                            user.disconnect();
                        }
                    });

                } catch (UnknownCommandException e) {
                    Logger.getLogger().log(Level.WARNING, "Получена команда, неизвестная серверу.");
                    senders.execute(() -> {
                        try {
                            connectionController.getRequestController()
                                    .sendError(user, "получена неизвестная серверу команда");
                        } catch (IOException ex) {
                            user.disconnect();
                        }
                    });

                } catch (ClassNotFoundException e) {
                    Logger.getLogger().log(Level.WARNING, "Получены неопознанные данные от клиента");
                    senders.execute(() -> {
                        try {
                            connectionController.getRequestController()
                                    .sendError(user, "получены неопознанные данные от клиента");
                        } catch (IOException ex) {
                            user.disconnect();
                        }
                    });

                }
            } catch (IOException e) {
                user.disconnect();
            }
            return null;
        });
        senders.execute(() -> {
            String reply = null;
            try {
                reply = futureReply.get();
            } catch (InterruptedException | ExecutionException ignored) {

            }
            sendUpdate();
            if (reply != null) {
                try {
                    connectionController.getRequestController().sendReply(user, reply);
                    Logger.getLogger().log(Level.INFO, "Отправлен ответ клиенту " + user.getLogin() + ".");
                } catch (IOException e) {
                    user.disconnect();
                }
            }
            listeners.execute(() -> listenRequests(user));
        });
    }

    public void sendUpdate() {
        users.removeIf(User::isDisconnected);
        for (User user : users) {
            try {
                connectionController.getRequestController().sendCollection(user,
                        dataController.getMap());
                Logger.getLogger().log(Level.INFO, "Отправлено обновление " + user.getLogin());
            } catch (IOException e) {
                user.disconnect();
            }
        }
    }

    /**
     * Execute command and add it in history
     *
     * @param command that need to invoke
     * @param args    for this command
     * @throws IncorrectArgumentException if requiring args is incorrect
     */
    protected String invoke(User user, final Command command, final Object args)
            throws IncorrectArgumentException, IOException, ClassNotFoundException {
        Logger.getLogger().log(Level.INFO, "Получена команда " + command.getName() + " от клиента " +
                (user.getLogin() == null ? user.getAddress() : user.getLogin()));
        return command.execute(user, this, args);
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

    public void stop() {
        listeners.shutdownNow();
        executors.shutdown();
        boolean done = false;
        try {
            done = executors.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {

        }
        if (done)
            Logger.getLogger().log(Level.INFO, "Все полученные команды были исполнены.");
        else
            Logger.getLogger().log(Level.INFO, "Не все команды были исполнены.");
        senders.shutdown();
    }

    public DataController getDataController() {
        return dataController;
    }

}
