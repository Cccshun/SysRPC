package serialization;


import common.constants.SerializerType;
import protocol.SysMessage;

public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        // TODO
        return null;
    }

    @Override
    public SysMessage deserialize(byte[] bytes, int msgType) {
        // TODO
        return null;
    }

    @Override
    public int getSerializerType() {
        return SerializerType.JSONSERIALIZER;
    }
}
