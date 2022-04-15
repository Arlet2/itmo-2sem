package connection_control;

import commands.CommandController;
import exceptions.MissingArgumentException;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Control connection with server
 */
public class ConnectionController {
    /**
     * Current program controller
     */
    private final CommandController commandController;
    /**
     * Current channel with server
     */
    private SocketChannel channel;
    /**
     * Selector for all channels
     */
    private Selector selector;
    /**
     * Port that client will connect
     */
    private int port;
    /**
     * Server address
     */
    private String address;

    /**
     * Create connection controller for connect to server
     * @param controller current program controller
     * @throws MissingArgumentException if config file haven't got address and port
     * @throws FileNotFoundException if config file not found
     */
    public ConnectionController(CommandController controller) throws MissingArgumentException, FileNotFoundException {
        this.commandController = controller;
        readConfig();
        try {
            selector = Selector.open();
            reopenChannel();
        } catch (IOException e) {
            System.out.println("Ошибка создания подключения");
        }
    }

    /**
     * Read config file with "config.excalibbur".
     * File need to have "port: *digits*" and "address: *IP address/domain*"
     * @throws FileNotFoundException if file not found
     * @throws MissingArgumentException if port or address not found in file
     */
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
        matcher = Pattern.compile("(?<=address:)[\\w\\d.]+|(?<=address:\\s)[\\w\\d.]+|(?<=address:\\s{2})[\\w\\d.]+",
                Pattern.CASE_INSENSITIVE).matcher(s.toString());
        if (matcher.find())
            address = s.substring(matcher.start(), matcher.end());
        else
            throw new MissingArgumentException("в файле конфигурации не был найден адрес сервера. Добавьте в файл строку типа\n" +
                    "\"address: localhost\", \"address: 192.65.3.5\"");
    }

    /**
     * Close current channel and open new
     * @throws IOException if new channel can't open or old can't close
     */
    public void reopenChannel() throws IOException {
        if (channel != null)
            channel.close();
        channel = openNewChannel();
    }

    /**
     * Wait on channels for new data
     * @return object with data
     * @throws IOException if object couldn't receive
     * @throws ClassNotFoundException if object couldn't deserialize
     */
    public Object processConnection() throws IOException, ClassNotFoundException {
        selector.select();
        for (SelectionKey key : selector.selectedKeys()) {
            if (key.isReadable()) {
                return receiveObject(channel);
            }
        }
        return null;
    }

    /**
     * Convert receiving object to Request
     * @return Request from server
     * @throws IOException if Request couldn't receive
     * @throws ClassNotFoundException if object couldn't deserialize
     */
    public Request receiveRequest() throws IOException, ClassNotFoundException {
        return (Request) processConnection();
    }

    /**
     * Send object to server
     * @param channel where server is
     * @param object that need to send
     * @throws IOException if object couldn't send
     */
    public void sendObject(SocketChannel channel, Object object) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(object);
        objOut.flush();
        channel.write(ByteBuffer.wrap(byteOut.toByteArray()));
        byteOut.reset();
    }

    /**
     * Receive data object from server
     * @param channel from where object sent
     * @return Object that was sent
     * @throws IOException if object couldn't receive
     * @throws ClassNotFoundException if object couldn't deserialize
     */
    private Object receiveObject(SocketChannel channel) throws IOException, ClassNotFoundException {
        ByteBuffer buff = ByteBuffer.allocate(1024 * 1024);
        channel.read(buff);
        ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(buff.array()));
        return objIn.readObject();
    }

    /**
     * Create new nonblocking channel and register it to selector
     * @return new channel
     * @throws IOException if channel couldn't open
     */
    private SocketChannel openNewChannel() throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        return channel;
    }

    /**
     * Try to create connection with server
     * @return <b>true</b> if connection is created else <b>false</b>
     */
    public boolean tryToConnect() {
        try {
            if (channel.connect(new InetSocketAddress(address, port)))
                return true;
            return channel.finishConnect();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Close connection with server
     * @throws IOException if closing connection is failed
     */
    public void disconnect() throws IOException {
        if (channel != null)
            channel.close();
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public Selector getSelector() {
        return selector;
    }

    public CommandController getCommandController() {
        return commandController;
    }
}
