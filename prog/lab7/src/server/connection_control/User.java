package server.connection_control;

import java.net.Socket;

public class User {
    private final Socket socket;
    private final String login;
    private boolean isConnected;
    public User(Socket socket, String login) {
        this.socket = socket;
        this.login = login;
        isConnected = true;
    }
    public void disconnect() {
        ConnectionController.disconnect(socket);
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getLogin() {
        return login;
    }
}
