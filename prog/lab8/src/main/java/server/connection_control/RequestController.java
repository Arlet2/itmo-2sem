package server.connection_control;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import server.commands.Command;
import connect_utils.DataTransferObject;
import connect_utils.Serializer;
import data_classes.City;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Control all requests between server and clients
 */
public class RequestController {

    private final ConnectionController connectionController;

    RequestController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    /**
     * Send OK request to user
     *
     * @throws IOException if server couldn't send this request
     */
    public void sendOK(User user) throws IOException {
        connectionController.sendObject(user, new DataTransferObject(DataTransferObject.Code.OK, ""));
    }

    /**
     * Send REPLY request to user
     *
     * @param msg that user can see as result of command execution
     * @throws IOException if server couldn't send this request
     */
    public void sendReply(User user, String msg) throws IOException {
        connectionController.sendObject(user, new DataTransferObject(DataTransferObject.Code.REPLY, msg));
    }

    /**
     * Get OK-reply
     *
     * @throws IOException if connection was closed
     */
    public void receiveOK(User user) throws IOException {
        try {
            connectionController.receiveObject(user, DataTransferObject.Code.OK);
        } catch (ClassNotFoundException ignored) {

        }
    }

    /**
     * Send ERROR request
     *
     * @param msg that user can see as explanation of error
     * @throws IOException if server couldn't send this request
     */
    public void sendError(User user, String msg) throws IOException {
        connectionController.sendObject(user, new DataTransferObject(DataTransferObject.Code.ERROR, msg));
    }

    public void sendCollection(User user, HashMap<Long, City> cities) throws IOException {
        DataTransferObject dto = new DataTransferObject(DataTransferObject.Code.COMMAND,
                Serializer.convertObjectToBytes(
                        new ArrayList<>(cities.values())), DataTransferObject.DataType.CITIES_ARRAY);
        connectionController.sendObject(user, dto);
    }

    /**
     * Receive request from user
     *
     * @return request
     * @throws IOException            if server couldn't receive this request
     * @throws ClassNotFoundException if server received not expected class
     */
    public DataTransferObject receiveRequest(User user, DataTransferObject.Code expectedCode)
            throws IOException, ClassNotFoundException {
        return connectionController.receiveObject(user, expectedCode);
    }

}
