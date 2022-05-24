package client.connection_control;

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

    /**
     * Send request to server
     *
     * @param dataTransferObject that need to send
     * @throws IOException if request couldn't send
     */
    public void sendRequest(DataTransferObject dataTransferObject) throws IOException {
        connectionController.sendObject(dataTransferObject);
    }

    public void sendOK() throws IOException {
        connectionController.sendObject(new DataTransferObject(DataTransferObject.Code.OK, ""));
    }
    /**
     * Send city object to server
     *
     * @param city    that need to send
     * @throws IOException if City object couldn't send
     */
    public void sendCity(City city) throws IOException {
        DataTransferObject dto = new DataTransferObject(
                DataTransferObject.Code.NOT_REQUEST, Serializer.convertObjectToBytes(city),
                DataTransferObject.DataType.CITY);
        connectionController.sendObject(dto);
    }

    public Collection<Command> getCommands() throws IOException, ClassNotFoundException {
        return (Collection<Command>) Serializer.convertBytesToObject(connectionController.processConnection()
                .getDataBytes());
    }

    public Collection<City> getCities() throws IOException, ClassNotFoundException {
        return (Collection<City>) Serializer.convertBytesToObject(connectionController.processConnection()
                .getDataBytes());
    }
}
