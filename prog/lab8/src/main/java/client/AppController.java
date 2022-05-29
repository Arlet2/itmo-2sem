package client;

import client.connection_control.ConnectionController;
import client.data_control.FileController;
import client.ui.UIController;
import connect_utils.CommandInfo;
import data_classes.City;
import connect_utils.DataTransferObject;
import connect_utils.Serializer;
import exceptions.ConfigFileNotFoundException;
import exceptions.ConnectionException;
import exceptions.MissingArgumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * controls execution of all commands
 */
public class AppController {
    /**
     * Constant of max value in history
     */
    public static final int MAX_COMMANDS_IN_HISTORY = 13;

    /**
     * controls connection with server
     */
    private final ConnectionController connectionController;

    private final UIController uiController = new UIController(this);

    private ArrayList<CommandInfo> history = new ArrayList<>();

    private FileController fileController = new FileController();

    private final ExecutorService listeners = Executors.newFixedThreadPool(3);

    /**
     * Create scanner and read configuration for connection
     * After that start connection with user
     */
    public AppController() throws MissingArgumentException, ConfigFileNotFoundException, ConnectionException {
        connectionController = new ConnectionController(this);
    }

    public void startWork() {
        uiController.createAuthWindow();
    }

    /**
     * connect to server
     */
    public boolean connect(String login, String password, boolean isLogin) throws ConnectionException {
        history = new ArrayList<>();
        try {
            connectionController.openChannel();
        } catch (IOException e) {
            throw new ConnectionException("net_channel_failed");
        }
        if (!connectionController.tryToConnect())
            throw new ConnectionException("connection_is_closed");
        try {
            if (isLogin)
                connectionController.getRequestController().sendLogin(login, password);
            else
                connectionController.getRequestController().sendRegister(login, password);
            DataTransferObject dataTransferObject = connectionController.getRequestController().receiveRequest();
            if (dataTransferObject.getCode() == DataTransferObject.Code.ERROR)
                throw new ConnectionException((String) Serializer.convertBytesToObject(
                        dataTransferObject.getDataBytes()));
            connectionController.getRequestController().sendOK();
        } catch (IOException e) {
            throw new ConnectionException("configuration_data_loading_failed");
        } catch (ClassNotFoundException e) {
            throw new ConnectionException("strange_request");
        }
        listeners.execute(this::listenerAction);
        return true;
    }

    private void listenerAction() {
        try {
            listenRequests();
        } catch (IOException e) {
            UIController.showErrorDialog("connection_is_closed");
        } catch (ClassNotFoundException e) {
            UIController.showErrorDialog("strange_request");
        }
    }

    private void listenRequests() throws IOException, ClassNotFoundException {
        DataTransferObject dto = connectionController.getRequestController().receiveRequest();
        listeners.execute(this::listenerAction);
        switch (dto.getCode()) {
            case ERROR:
                String error = (String) Serializer.convertBytesToObject(dto.getDataBytes());
                UIController.showErrorDialog(error);
                break;
            case REPLY:
                String reply = (String) Serializer.convertBytesToObject(dto.getDataBytes());
                UIController.showInfoDialog(reply);
                break;
            case COMMAND:
                uiController.updateData((Collection<City>) Serializer.convertBytesToObject(dto.getDataBytes()));
                break;
            default:
                break;
        }
    }

    public void addCommandToHistory(CommandInfo command) {
        if (command.getName().equals("login") || command.getName().equals("register"))
            return;
        if (history.size() == MAX_COMMANDS_IN_HISTORY) {
            history.remove(0);
        }
        history.add(command);
    }

    /**
     * End program execution
     */
    public void exit() {
        try {
            getConnectionController().disconnect();
        } catch (IOException ignored) {

        }
        System.exit(0);
    }

    public ArrayList<CommandInfo> getHistory() {
        return history;
    }

    public ConnectionController getConnectionController() {
        return connectionController;
    }

    public FileController getFileController() {
        return fileController;
    }
}
