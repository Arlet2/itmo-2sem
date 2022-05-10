package server.connection_control;

import connect_utils.CommandInfo;
import connect_utils.Request;
import data_classes.City;

import java.io.IOException;
import java.util.ArrayList;

public class RequestController {
    private final ConnectionController connectionController;
    RequestController (ConnectionController connectionController) {
        this.connectionController = connectionController;
    }
    /**
     * Send OK request to user
     *
     * @throws IOException if server couldn't send this request
     */
    public void sendOK() throws IOException {
        connectionController.sendRequest(new Request(Request.RequestCode.OK, ""));
    }

    /**
     * Send REPLY request to user
     *
     * @param msg that user can see as result of command execution
     * @throws IOException if server couldn't send this request
     */
    public void sendReply(String msg) throws IOException {
        connectionController.sendRequest(new Request(Request.RequestCode.REPLY, msg));
    }
    public void receiveOK() throws IOException {
        try {
            receiveRequest();
        } catch (ClassNotFoundException e) {

        }
    }
    /**
     * Send ERROR request
     *
     * @param msg that user can see as explanation of error
     * @throws IOException if server couldn't send this request
     */
    public void sendError(String msg) throws IOException {
        connectionController.sendRequest(new Request(Request.RequestCode.ERROR, msg + "\n"));
    }
    public void sendCommandList(ArrayList<CommandInfo> list) throws IOException {
        connectionController.sendObject(list);
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
    public City receiveCity() throws IOException, ClassNotFoundException {
        return (City) connectionController.receiveObject();
    }
}
