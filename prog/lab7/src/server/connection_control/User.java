package server.connection_control;

import server.Logger;
import server.commands.Command;

import java.net.Socket;
import java.util.ArrayList;

public class User {
    public static final int MAX_COMMANDS_IN_HISTORY = 13;
    private final ArrayList<Command> history = new ArrayList<>();
    private final Socket socket;
    private volatile String login;
    private volatile boolean isConnected;

    public User(Socket socket, String login) {
        this.socket = socket;
        this.login = login;
        isConnected = true;
    }

    public void disconnect() {
        Logger.logDisconnect(this);
        ConnectionController.disconnect(socket);
        isConnected = false;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void addCommandToHistory(Command command) {
        if (history.size() == MAX_COMMANDS_IN_HISTORY) {
            history.remove(0);
        }
        history.add(command);
    }

    public ArrayList<Command> getHistory() {
        return history;
    }

    public boolean isDisconnected() {
        return !isConnected;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getLogin() {
        return login;
    }

    public String getAddress() {
        return socket.getInetAddress().getHostAddress();
    }
}
