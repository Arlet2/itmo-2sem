package client.connection_control;

import connect_utils.CommandInfo;
import server.commands.Command;
import connect_utils.DataTransferObject;
import connect_utils.Serializer;
import data_classes.City;

import java.io.IOException;
import java.util.Collection;

/**
 * Class uses for control all requests
 */
public class RequestController {
    private final ConnectionController connectionController;

    RequestController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    /**
     * Convert receiving object to Request
     *
     * @return Request from server
     * @throws IOException            if Request couldn't receive
     * @throws ClassNotFoundException if object couldn't deserialize
     */
    public DataTransferObject receiveRequest() throws IOException, ClassNotFoundException {
        return connectionController.processConnection();
    }

    public void sendCommand(CommandInfo command) throws IOException {
        connectionController.getAppController().addCommandToHistory(command);
        connectionController.sendObject(new DataTransferObject(DataTransferObject.Code.COMMAND,
                Serializer.convertObjectToBytes(command),
                DataTransferObject.DataType.MESSAGE));
    }

    public void sendLogin(String login, String password) throws IOException {
        String[] args = {"", login, password};
        sendCommand(new CommandInfo("login", Serializer.convertObjectToBytes(args)));
    }

    public void sendRegister(String login, String password) throws IOException {
        String[] args = {"", login, password};
        sendCommand(new CommandInfo("register", Serializer.convertObjectToBytes(args)));
    }

    public void sendOK() throws IOException {
        connectionController.sendObject(new DataTransferObject(DataTransferObject.Code.OK, ""));
    }
}
