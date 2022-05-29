package connect_utils;

import java.io.*;

public class Serializer {
    public static byte[] convertObjectToBytes(Object object) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        return byteStream.toByteArray();
    }

    public static Object convertBytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        return new ObjectInputStream(byteInputStream).readObject();
    }
}
