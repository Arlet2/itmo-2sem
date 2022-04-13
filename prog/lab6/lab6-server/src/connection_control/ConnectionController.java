package connection_control;

import commands.CommandController;
import exceptions.MissingArgumentException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionController {
    private Socket socket;
    private ServerSocket serverSocket;
    private boolean isConnected = false;
    private int port;
    private final CommandController commandController;
    public ConnectionController(CommandController commandController) throws FileNotFoundException, MissingArgumentException {
        this.commandController = commandController;
        readConfig();
    }
    public void start () throws IOException {
        serverSocket = new ServerSocket(port);
        commandController.getLogger().log(Level.INFO, "Сервер запущен на порте "+port+".");

    }
    private void readConfig() throws FileNotFoundException, MissingArgumentException {
        Scanner scanner = new Scanner(new FileInputStream("config.excalibbur"));
        StringBuilder s = new StringBuilder();
        while(scanner.hasNextLine())
            s.append(scanner.nextLine()).append("\n");
        Matcher matcher = Pattern.compile("(?<=port:)\\d+|(?<=port:\\s)\\d+|(?<=port:\\s{2})\\d+",Pattern.CASE_INSENSITIVE).matcher(s.toString());
        scanner.close();
        if (matcher.find())
            port = Integer.parseInt(s.substring(matcher.start(), matcher.end()));
        else
            throw new MissingArgumentException("в файле конфигурации не был найден порт. Добавьте в файл строку типа \"port: 1234\"");
    }

    public void connect () throws IOException {
        isConnected = false;
        socket = serverSocket.accept();
        commandController.getLogger().log(Level.INFO, "Принято подключение от клиента "+socket.getInetAddress()+":"+socket.getLocalPort()+".");
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

    public CommandController getCommandController() {
        return commandController;
    }
}