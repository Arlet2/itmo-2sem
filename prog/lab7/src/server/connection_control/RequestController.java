package server.connection_control;

import connect_utils.CommandInfo;
import connect_utils.Request;
import data_classes.City;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

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
    public void sendOK(Socket socket) throws IOException {
        connectionController.sendRequest(socket, new Request(Request.RequestCode.OK, ""));
    }

    /**
     * Send REPLY request to user
     *
     * @param msg that user can see as result of command execution
     * @throws IOException if server couldn't send this request
     */
    public void sendReply(Socket socket, String msg) throws IOException {
        connectionController.sendRequest(socket, new Request(Request.RequestCode.REPLY, msg));
    }

    public void receiveOK(Socket socket) throws IOException {
        try {
            receiveRequest(socket);
        } catch (ClassNotFoundException ignored) {

        }
    }

    /**
     * Send ERROR request
     *
     * @param msg that user can see as explanation of error
     * @throws IOException if server couldn't send this request
     */
    public void sendError(Socket socket, String msg) throws IOException {
        connectionController.sendRequest(socket, new Request(Request.RequestCode.ERROR, msg + "\n"));
    }

    public void sendCommandList(Socket socket, ArrayList<CommandInfo> list) throws IOException {
        connectionController.sendObject(socket, list);
    }

    /**
     * Receive request from user
     *
     * @return request
     * @throws IOException            if server couldn't receive this request
     * @throws ClassNotFoundException if server received not expected class
     */
    public Request receiveRequest(Socket socket) throws IOException, ClassNotFoundException {
        return (Request) connectionController.receiveObject(socket);
    }

    public City receiveCity(Socket socket) throws IOException, ClassNotFoundException {
        return (City) connectionController.receiveObject(socket);
    }
}
