package serialization;

import common.constants.MessageType;
import common.constants.SerializerType;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import protocol.Request;
import protocol.Response;
import protocol.SysMessage;


public class ProtostuffSerializer implements Serializer {
    private static final LinkedBuffer buffer = LinkedBuffer.allocate(1024);

    @Override
    @SuppressWarnings("unchecked")
    public <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        buffer.clear();
        return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
    }

    @Override
    public SysMessage deserialize(byte[] bytes, int msgType) {
        if (msgType == MessageType.REQUEST) {
            Request request = new Request();
            ProtostuffIOUtil.mergeFrom(bytes, request, RuntimeSchema.getSchema(Request.class));
            return request;
        } else if (msgType == MessageType.RESPONSE) {
            Response response = new Response();
            ProtostuffIOUtil.mergeFrom(bytes, response, RuntimeSchema.getSchema(Response.class));
            return response;
        } else {
            throw new RuntimeException("Message type is not supported");
        }
    }

    @Override
    public int getSerializerType() {
        return SerializerType.PROTOSTUFFERIALIZER;
    }
}
