package server.connection_control;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import exceptions.ConfigFileNotFoundException;
import server.Logger;
import server.commands.CommandController;
import exceptions.MissingArgumentException;
import connect_utils.Request;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * controls connections with users
 */
public class ConnectionController {
    /**
     * current socket with user
     */
    private Socket socket;

    /**
     * socket's factory
     */
    private ServerSocket serverSocket;

    /**
     * check current connection with user
     * can be true if connection will be interrupted
     */
    private boolean isConnected = false;

    /**
     * current port where server is located
     */
    private final int port;

    /**
     * command controller that controls this program
     */
    private final CommandController commandController;

    private final RequestController requestController = new RequestController(this);;

    /**
     * Create new connection controller for connecting with users.
     * Reading config file with name "config.excalibbur" with connection's data
     * File need to have "port: *digits*"
     *
     * @param commandController current controller
     * @throws MissingArgumentException if port couldn't find in config file
     */
    public ConnectionController(CommandController commandController)
            throws ConfigFileNotFoundException, MissingArgumentException {
        this.commandController = commandController;
        port = commandController.getDataController().getFilesController().readConfigPort();
    }

    /**
     * Start working of server
     *
     * @throws IOException if port is using now
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        Logger.getLogger().log(Level.INFO, "Сервер запущен на порте " + port + ".");
    }

    /**
     * Create new connection with user
     *
     * @throws IOException if connection was failed
     */
    public void connect() throws IOException {
        isConnected = false;
        socket = serverSocket.accept();
        Logger.getLogger().log(Level.INFO, "Принято подключение от клиента "
                + socket.getInetAddress() + ":" + socket.getLocalPort() + ".");
        isConnected = true;
    }

    /**
     * Receive some data from client
     *
     * @return object (can be City or Request)
     * @throws IOException            if receiving is failed
     * @throws ClassNotFoundException if object can't be serialized
     */
    protected Object receiveObject() throws IOException, ClassNotFoundException {
        return new ObjectInputStream(socket.getInputStream()).readObject();
    }

    /**
     * Send request to client
     * Data will be divided if data is more than buffer size
     *
     * @param request that was sent to client
     * @throws IOException if sending is failed
     */
    protected void sendRequest(Request request) throws IOException {
        final int byteSize = 10;
        if (request.getMsgBytes().length > byteSize) {
            int parts = request.getMsg().getBytes().length / byteSize;
            for (int partCount = 0; partCount < parts; partCount++) {
                sendObject(new Request(Request.RequestCode.PART_OF_DATE,
                        Arrays.copyOfRange(request.getMsg().getBytes(), byteSize * partCount,
                                (partCount + 1) * byteSize)));
                requestController.receiveOK();
            }
            if (request.getMsg().getBytes().length % byteSize != 0)
                sendObject(new Request(Request.RequestCode.PART_OF_DATE, Arrays.copyOfRange(request.getMsg().getBytes(),
                        parts * byteSize, request.getMsgBytes().length)));
            requestController.receiveOK();
            sendRequest(new Request(request.getRequestCode(), ""));
        } else
            sendObject(request);
    }

    /**
     * Send data to client
     *
     * @param object that need to send to client
     * @throws IOException if sending is failed
     */
    protected void sendObject(Object object) throws IOException {
        ObjectOutputStream objOut = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        objOut.writeObject(object);
        objOut.flush();
    }

    /**
     * Close connection with client
     *
     * @throws IOException if something wrong with connection
     */
    public void disconnect() throws IOException {
        if (socket != null)
            socket.close();
        isConnected = false;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public CommandController getCommandController() {
        return commandController;
    }

    public RequestController getRequestController() {
        return requestController;
    }
}