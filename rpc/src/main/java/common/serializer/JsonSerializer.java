package common.serializer;


import common.constants.SerializerType;

public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        // TODO
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        // TODO
        return null;
    }

    @Override
    public int getSerializerType() {
        return SerializerType.JSONSERIALIZER;
    }
}
