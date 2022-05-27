package server.connection_control;

import connect_utils.DataTransferObject;
import server.Logger;
import server.commands.Command;

import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Class that store login, history of command and socket of user
 */
public class User {

    /**
     * Socket of this user
     */
    private final Socket socket;

    /**
     * Login of this user
     */
    private volatile String login;

    /**
     * Connection status
     */
    private volatile boolean isConnected;

    private final LinkedList<DataTransferObject> requests = new LinkedList<>();

    /**
     * Create user
     *
     * @param socket of this user
     * @param login  of this user
     */
    public User(Socket socket, String login) {
        this.socket = socket;
        this.login = login;
        isConnected = true;
    }

    /**
     * Close connection with user
     * Switch isConnected to false
     */
    public void disconnect() {
        Logger.logDisconnect(this);
        ConnectionController.disconnect(socket);
        isConnected = false;
    }

    /**
     * Set login of user
     *
     * @param login that need to set
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Get status of this user
     * After creation is true but after using disconnect() set to false
     *
     * @return status of user's connection
     */
    public boolean isDisconnected() {
        return !isConnected;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getLogin() {
        return login;
    }

    /**
     * Get IP-address of user
     *
     * @return user's address
     */
    public String getAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    public DataTransferObject searchAndDeleteRequestByCode(DataTransferObject.Code code) {
        for (DataTransferObject dto: requests) {
            if (dto.getCode() == code) {
                requests.remove(dto);
                return dto;
            }
        }
        return null;
    }

    public void addDataTransferObject(DataTransferObject dto) {
        requests.add(dto);
    }

}
