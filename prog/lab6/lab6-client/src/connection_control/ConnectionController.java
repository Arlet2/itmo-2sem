package connection_control;

import commands.CommandController;
import data_classes.City;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ConnectionController {
    private final CommandController commandController;
    private SocketChannel channel;
    private Selector selector;

    public ConnectionController(CommandController controller) {
        this.commandController = controller;
        try {
            selector = Selector.open();
            reopenChannel();
        } catch (IOException e) {
            System.out.println("Ошибка создания подключения");
        }
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

    public City receiveCity() throws IOException, ClassNotFoundException {
        return (City) processConnection();
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
            if (channel.connect(new InetSocketAddress("localhost", 2347)))
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
