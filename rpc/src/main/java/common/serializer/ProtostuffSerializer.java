package common.serializer;

import common.constants.MessageType;
import common.constants.SerializerType;
import common.message.Request;
import common.message.Response;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;


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
    public Object deserialize(byte[] bytes, int messageType) {
        if (messageType == MessageType.REQUEST) {
            Request request;
            try {
                request = Request.class.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            ProtostuffIOUtil.mergeFrom(bytes, request, RuntimeSchema.getSchema(Request.class));
            return request;
        } else if (messageType == MessageType.RESPONSE) {
            Response response;
            try {
                response = Response.class.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
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
