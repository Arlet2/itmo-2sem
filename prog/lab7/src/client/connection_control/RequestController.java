package client.connection_control;

import connect_utils.CommandInfo;
import connect_utils.Request;
import data_classes.City;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class RequestController {
    private final ConnectionController connectionController;
    RequestController (ConnectionController connectionController) {
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
        StringBuilder stringBuilder = new StringBuilder();
        while (request.getRequestCode() == Request.RequestCode.PART_OF_DATE) {
            stringBuilder.append(new String(request.getMsgBytes()));
            request = (Request) connectionController.processConnection();
        }
        return new Request(request.getRequestCode(), stringBuilder.toString());
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
