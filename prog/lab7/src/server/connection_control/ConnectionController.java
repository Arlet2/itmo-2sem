package server.connection_control;

import exceptions.ConfigFileNotFoundException;
import server.Logger;
import exceptions.MissingArgumentException;
import connect_utils.Request;
import server.data_control.FilesController;

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
     * socket's factory
     */
    private ServerSocket serverSocket;

    /**
     * current port where server is located
     */
    private final int port;

    private final RequestController requestController = new RequestController(this);

    /**
     * Create new connection controller for connecting with users.
     * Reading config file with name "config.excalibbur" with connection's data
     * File need to have "port: *digits*"
     *
     * @throws MissingArgumentException if port couldn't find in config file
     */
    public ConnectionController()
            throws ConfigFileNotFoundException, MissingArgumentException {
        port = FilesController.readConfigPort();
    }

    /**
     * Start working of server
     *
     * @throws IOException if port is using now
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        Logger.getLogger().log(Level.INFO, "Сервер запущен на порте " + port + " и готов принимать клиентов.");
    }

    /**
     * Create new connection with user
     *
     * @throws IOException if connection was failed
     */
    public Socket connect() throws IOException {
        Socket socket = serverSocket.accept();
        Logger.getLogger().log(Level.INFO, "Принято подключение от клиента "
                + socket.getInetAddress() + ":" + socket.getLocalPort() + ".");
        return socket;
    }

    /**
     * Receive some data from client
     *
     * @return object (can be City or Request)
     * @throws IOException            if receiving is failed
     * @throws ClassNotFoundException if object can't be serialized
     */
    protected Object receiveObject(Socket socket) throws IOException, ClassNotFoundException {
        return new ObjectInputStream(socket.getInputStream()).readObject();
    }

    /**
     * Send request to client
     * Data will be divided if data is more than buffer size
     *
     * @param request that was sent to client
     * @throws IOException if sending is failed
     */
    protected void sendRequest(Socket socket, Request request) throws IOException {
        final int byteSize = 2048; // 2048
        if (request.getMsgBytes().length > byteSize) {
            int parts = request.getMsg().getBytes().length / byteSize;
            for (int partCount = 0; partCount < parts; partCount++) {
                sendObject(socket, new Request(Request.RequestCode.PART_OF_DATE,
                        Arrays.copyOfRange(request.getMsg().getBytes(), byteSize * partCount,
                                (partCount + 1) * byteSize)));
                requestController.receiveOK(socket);
            }
            if (request.getMsg().getBytes().length % byteSize != 0)
                sendObject(socket, new Request(Request.RequestCode.PART_OF_DATE, Arrays.copyOfRange(request.getMsg().getBytes(),
                        parts * byteSize, request.getMsgBytes().length)));
            requestController.receiveOK(socket);
            sendRequest(socket, new Request(request.getRequestCode(), ""));
        } else
            sendObject(socket, request);
    }

    /**
     * Send data to client
     *
     * @param object that need to send to client
     * @throws IOException if sending is failed
     */
    protected void sendObject(Socket socket, Object object) throws IOException {
        ObjectOutputStream objOut = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        objOut.writeObject(object);
        objOut.flush();
    }

    /**
     * Close connection with client
     */
    public static void disconnect(Socket socket) {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException ignored) {

        }
    }

    public RequestController getRequestController() {
        return requestController;
    }
}