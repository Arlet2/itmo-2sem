package client.connection_control;

import connect_utils.CommandInfo;
import connect_utils.Request;
import data_classes.City;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

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
    public Request receiveRequest() throws IOException, ClassNotFoundException {
        Request request = (Request) connectionController.processConnection();
        if (request.getRequestCode() != Request.RequestCode.PART_OF_DATE)
            return request;
        ArrayList<Byte> bytes = new ArrayList<>();
        while (request.getRequestCode() == Request.RequestCode.PART_OF_DATE) {
            for (byte b : request.getMsgBytes()) {
                bytes.add(b);
            }
            sendRequest(connectionController.getChannel(), new Request(Request.RequestCode.OK, ""));
            request = (Request) connectionController.processConnection();
        }
        byte[] fbytes = new byte[bytes.size()];
        for (int i = 0; i < fbytes.length; i++) {
            fbytes[i] = bytes.get(i);
        }
        return new Request(request.getRequestCode(), new String(fbytes));
    }

    /**
     * Send request to server
     *
     * @param channel that connect with server
     * @param request that need to send
     * @throws IOException if request couldn't send
     */
    public void sendRequest(SocketChannel channel, Request request) throws IOException {
        connectionController.sendObject(channel, request);
    }

    /**
     * Send city object to server
     *
     * @param channel that connect with server
     * @param city    that need to send
     * @throws IOException if City object couldn't send
     */
    public void sendCity(SocketChannel channel, City city) throws IOException {
        connectionController.sendObject(channel, city);
    }

    public ArrayList<CommandInfo> getCommandInfos() throws IOException, ClassNotFoundException {
        return (ArrayList<CommandInfo>) connectionController.processConnection();
    }
}
