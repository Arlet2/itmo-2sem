package connection_control;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionController {
    private Socket socket;
    private ServerSocket serverSocket;
    private boolean isConnected = false;

    public void start () throws IOException {
        serverSocket = new ServerSocket(2347);
        System.out.println("Сервер запущен.");
    }

    public void connect () throws IOException {
        isConnected = false;
        socket = serverSocket.accept();
        System.out.println("Принято подключение от клиента.");
        isConnected = true;
    }

    public Object receiveObject () throws IOException, ClassNotFoundException {
        return new ObjectInputStream(socket.getInputStream()).readObject();
    }

    public void sendObject (Object object) throws IOException {
        ObjectOutputStream objOut = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        objOut.writeObject(object);
        objOut.flush();
    }
    public void disconnect () throws IOException {
        if (socket != null)
            socket.close();
        isConnected = false;
    }
    public void close() {
        try {
            if(serverSocket != null)
                serverSocket.close();
            if(socket != null)
                socket.close();
        } catch (IOException e) {

        }
        isConnected = false;
    }
    public Socket getSocket() {
        return socket;
    }

    public boolean isConnected() {
        return isConnected;
    }
}