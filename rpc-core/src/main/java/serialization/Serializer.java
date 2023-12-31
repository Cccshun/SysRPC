package serialization;

import common.constants.SerializerType;
import common.exception.RpcError;
import common.exception.RpcException;
import io.protostuff.Rpc;
import protocol.SysMessage;

public interface Serializer {
    <T> byte[] serialize(T obj);

     SysMessage deserialize(byte[] bytes, int msgType);

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
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
    }
}
