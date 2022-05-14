package client.connection_control;

import client.commands.CommandController;
import exceptions.ConfigFileNotFoundException;
import exceptions.ConnectionException;
import exceptions.MissingArgumentException;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Control connection with server
 */
public class ConnectionController {

    /**
     * Current channel with server
     */
    private SocketChannel channel;

    /**
     * Selector for all channels
     */
    private final Selector selector;

    /**
     * Server address
     */
    private final InetSocketAddress address;

    private final RequestController requestController = new RequestController(this);

    /**
     * Create connection controller for connect to server
     *
     * @param controller current program controller
     * @throws MissingArgumentException if config file haven't got address and port
     */
    public ConnectionController(CommandController controller) throws MissingArgumentException,
            ConfigFileNotFoundException, ConnectionException {
        address = controller.getFileController().readConfig();
        try {
            selector = Selector.open();
            openChannel();
        } catch (IOException e) {
            throw new ConnectionException("Ошибка создания подключения");
        }
    }

    /**
     * Close current channel and open new
     *
     * @throws IOException if new channel can't open or old can't close
     */
    public void openChannel() throws IOException {
        if (channel != null)
            channel.close();
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * Wait on channels for new data
     *
     * @return object with data
     * @throws IOException            if object couldn't receive
     * @throws ClassNotFoundException if object couldn't deserialize
     */
    protected Object processConnection() throws IOException, ClassNotFoundException {
        selector.select();
        for (SelectionKey key : selector.selectedKeys()) {
            if (key.isReadable()) {
                return receiveObject(channel);
            }
        }
        return null;
    }

    /**
     * Send object to server
     *
     * @param channel where server is
     * @param object  that need to send
     * @throws IOException if object couldn't send
     */
    protected void sendObject(SocketChannel channel, Object object) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(object);
        objOut.flush();
        channel.write(ByteBuffer.wrap(byteOut.toByteArray()));
        byteOut.reset();
    }

    /**
     * Receive data object from server
     *
     * @param channel from where object sent
     * @return Object that was sent
     * @throws IOException            if object couldn't receive
     * @throws ClassNotFoundException if object couldn't deserialize
     */
    private Object receiveObject(SocketChannel channel) throws IOException, ClassNotFoundException {
        ByteBuffer buff = ByteBuffer.allocate(4096);
        channel.read(buff);
        ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(buff.array()));
        return objIn.readObject();
    }

    /**
     * Try to create connection with server
     *
     * @return <b>true</b> if connection is created else <b>false</b>
     */
    public boolean tryToConnect() {
        try {
            if (channel.connect(address))
                return true;
            return channel.finishConnect();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Close connection with server
     *
     * @throws IOException if closing connection is failed
     */
    public void disconnect() throws IOException {
        if (channel != null)
            channel.close();
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public RequestController getRequestController() {
        return requestController;
    }
}
