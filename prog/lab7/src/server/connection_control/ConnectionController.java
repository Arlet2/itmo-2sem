package server.connection_control;

import server.commands.CommandController;
import connect_utils.CommandInfo;
import exceptions.MissingArgumentException;
import connect_utils.Request;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private int port;

    /**
     * command controller that controls this program
     */
    private final CommandController commandController;

    /**
     * Create new connection controller for connecting with users.
     * Reading config file with name "config.excalibbur" with connection's data
     * File need to have "port: *digits*"
     *
     * @param commandController current controller
     * @throws FileNotFoundException    if config file couldn't find
     * @throws MissingArgumentException if port couldn't find in config file
     */
    public ConnectionController(CommandController commandController)
            throws FileNotFoundException, MissingArgumentException {
        this.commandController = commandController;
        readConfig();
    }

    /**
     * Start working of server
     *
     * @throws IOException if port is using now
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        commandController.getLogger().log(Level.INFO, "Сервер запущен на порте " + port + ".");
    }

    /**
     * Read config file for connection
     *
     * @throws FileNotFoundException    if config file not exists
     * @throws MissingArgumentException if program couldn't read port in config file
     */
    private void readConfig() throws FileNotFoundException, MissingArgumentException {
        Scanner scanner = new Scanner(new FileInputStream("config.excalibbur"));
        StringBuilder s = new StringBuilder();
        while (scanner.hasNextLine())
            s.append(scanner.nextLine()).append("\n");
        Matcher matcher = Pattern.compile("(?<=port:)\\d+|(?<=port:\\s)\\d+|(?<=port:\\s{2})\\d+",
                Pattern.CASE_INSENSITIVE).matcher(s.toString());
        scanner.close();
        if (matcher.find())
            port = Integer.parseInt(s.substring(matcher.start(), matcher.end()));
        else
            throw new MissingArgumentException("в файле конфигурации не был найден порт. " +
                    "Добавьте в файл строку типа \"port: 1234\"");
    }

    /**
     * Create new connection with user
     *
     * @throws IOException if connection was failed
     */
    public void connect() throws IOException {
        isConnected = false;
        socket = serverSocket.accept();
        commandController.getLogger().log(Level.INFO, "Принято подключение от клиента "
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
    public Object receiveObject() throws IOException, ClassNotFoundException {
        return new ObjectInputStream(socket.getInputStream()).readObject();
    }

    /**
     * Send request to client
     * Data will be divided if data is more than buffer size
     *
     * @param request that was sent to client
     * @throws IOException if sending is failed
     */
    public void sendRequest(Request request) throws IOException {
        if (request.getMsgBytes().length > 2048) {
            int byteCounter = 0;
            int parts = request.getMsg().getBytes().length / 2048;
            for (int partCount = 0; partCount < parts - 1; partCount++) {
                sendObject(new Request(Request.RequestCode.PART_OF_DATE,
                        Arrays.copyOfRange(request.getMsg().getBytes(), 2048 * partCount + byteCounter,
                                (partCount + 1) * 2048)));
                byteCounter = 1;
            }
            if (request.getMsg().getBytes().length % 2048 != 0)
                sendObject(new Request(Request.RequestCode.PART_OF_DATE, Arrays.copyOfRange(request.getMsg().getBytes(),
                        parts * 2048 + 1, request.getMsgBytes().length)));
            sendRequest(new Request(request.getRequestCode(), ""));
        } else
            sendObject(request);
    }

    public void sendCommandList(ArrayList<CommandInfo> list) throws IOException {
        sendObject(list);
    }

    /**
     * Send data to client
     *
     * @param object that need to send to client
     * @throws IOException if sending is failed
     */
    private void sendObject(Object object) throws IOException {
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
}