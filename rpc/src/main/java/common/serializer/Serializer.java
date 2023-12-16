package common.serializer;

import common.constants.SerializerType;

public interface Serializer {
    <T> byte[] serialize(T obj);

    Object deserialize(byte[] bytes, int messageType);

    int getSerializerType();

    static Serializer getSerializer(int code) {
        if (code == SerializerType.JDKSERIALIZER) {
            return new JdkSerializer();
        } else if (code == SerializerType.JSONSERIALIZER) {
            return new JsonSerializer();
        } else if (code == SerializerType.KRYOSERIALIZER) {
            return new KryoSerializer();
        } else if (code == SerializerType.PROTOSTUFFERIALIZER) {
            return new ProtostuffSerializer();
        } else {
            return null;
        }
    }
}