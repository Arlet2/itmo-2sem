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

public class ConnectionController {
    private final CommandController commandController;
    private SocketChannel channel;
    private Selector selector;
    private int port;
    private String address;

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

    public void reopenChannel() throws IOException {
        if (channel != null)
            channel.close();
        channel = openNewChannel();
    }

    public Object processConnection() throws IOException, ClassNotFoundException {
        selector.select();
        for (SelectionKey key : selector.selectedKeys()) {
            if (key.isReadable()) {
                return receiveObject(channel);
            }
        }
        return null;
    }

    public Request receiveRequest() throws IOException, ClassNotFoundException {
        return (Request) processConnection();
    }

    public void sendObject(SocketChannel channel, Object object) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(object);
        objOut.flush();
        channel.write(ByteBuffer.wrap(byteOut.toByteArray()));
        byteOut.reset();
    }

    private Object receiveObject(SocketChannel channel) throws IOException, ClassNotFoundException {
        ByteBuffer buff = ByteBuffer.allocate(1024 * 1024);
        channel.read(buff);
        ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(buff.array()));
        return objIn.readObject();
    }

    private SocketChannel openNewChannel() throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        return channel;
    }

    public boolean tryToConnect() {
        try {
            if (channel.connect(new InetSocketAddress(address, port)))
                return true;
            return channel.finishConnect();
        } catch (IOException e) {
            return false;
        }
    }

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
