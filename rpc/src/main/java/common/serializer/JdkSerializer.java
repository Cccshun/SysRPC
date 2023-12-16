package common.serializer;

import common.constants.SerializerType;

import java.io.*;

public class JdkSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    @Override
    public int getSerializerType() {
        return SerializerType.JDKSERIALIZER;
    }
}
