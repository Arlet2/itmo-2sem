package client.connection_control;

import client.AppController;
import connect_utils.DataTransferObject;
import connect_utils.Serializer;
import exceptions.ConfigFileNotFoundException;
import exceptions.ConnectionException;
import exceptions.MissingArgumentException;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Control connection with server
 */
public class ConnectionController {

    private final AppController appController;

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

    /**
     * Controller that controls all requests
     */
    private final RequestController requestController = new RequestController(this);

    private final ReentrantLock connectionLock = new ReentrantLock();

    /**
     * Create connection controller for connect to server
     *
     * @param controller current program controller
     * @throws MissingArgumentException if config file haven't got address and port
     */
    public ConnectionController(AppController controller) throws MissingArgumentException,
            ConfigFileNotFoundException, ConnectionException {
        this.appController = controller;
        address = controller.getFileController().readConfig();
        try {
            selector = Selector.open();
            openChannel();
        } catch (IOException e) {
            throw new ConnectionException("create_connect_failed");
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
    protected DataTransferObject processConnection() throws IOException, ClassNotFoundException {
        selector.select();
        for (SelectionKey key : selector.selectedKeys()) {
            if (key.isReadable()) {
                return receiveObject();
            }
        }
        return null;
    }

    /**
     * Send object to server
     *
     * @param object that need to send
     * @throws IOException if object couldn't send
     */
    protected void sendObject(DataTransferObject object) throws IOException {
        connectionLock.lock();
        channel.write(ByteBuffer.wrap(Serializer.convertObjectToBytes(object)));
        connectionLock.unlock();
    }

    private DataTransferObject createObjectFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        return (DataTransferObject) new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
    }

    /**
     * Receive data object from server
     *
     * @return Object that was sent
     * @throws IOException            if object couldn't receive
     * @throws ClassNotFoundException if object couldn't deserialize
     */
    private DataTransferObject receiveObject() throws IOException, ClassNotFoundException {
        connectionLock.lock();
        final int byteSize = 4096;
        ByteBuffer buffer = ByteBuffer.allocate(byteSize * 2);
        channel.read(buffer);
        DataTransferObject mainObject = createObjectFromBytes(buffer.array());
        if (mainObject.getCode() == DataTransferObject.Code.PART_OF_DATE) {
            ByteBuffer dataBuffer = ByteBuffer.allocate(byteSize * 2);
            DataTransferObject dto = mainObject;
            do {
                if (dataBuffer.position() + dto.getDataBytes().length > dataBuffer.limit())
                    dataBuffer = ByteBuffer.allocate(dataBuffer.limit() * 2).put(dataBuffer.array());
                dataBuffer.put(dto.getDataBytes());
                channel.write(ByteBuffer.wrap(Serializer.convertObjectToBytes(
                        new DataTransferObject(DataTransferObject.Code.OK, "")
                )));
                selector.select();
                buffer.position(0);
                channel.read(buffer);
                dto = createObjectFromBytes(buffer.array());
            } while (dto.getCode() == DataTransferObject.Code.PART_OF_DATE);
            connectionLock.unlock();
            return new DataTransferObject(dto.getCode(), Arrays.copyOfRange(dataBuffer.array(), 0,
                    dataBuffer.position()), dto.getDataType());
        } else {
            connectionLock.unlock();
            return mainObject;
        }
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

    public RequestController getRequestController() {
        return requestController;
    }

    public AppController getAppController() {
        return appController;
    }
}
