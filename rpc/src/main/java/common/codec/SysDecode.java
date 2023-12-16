package common.codec;

import common.constants.MessageType;
import common.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SysDecode extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        // decode data type from message in 2 byte
        short messageType = byteBuf.readShort();
        if (messageType != MessageType.REQUEST &&
                messageType != MessageType.RESPONSE) {
            throw new RuntimeException("Error message type");
        }

        // decode serialize type from message in 2 type
        short serializerType = byteBuf.readShort();
        Serializer serializer = Serializer.getSerializer(serializerType);
        if (serializer == null) {
            throw new RuntimeException("This serializer type is not supported");
        }

        // decode data length and data from message, respectively
        int len = byteBuf.readInt();
        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);

        Object data = serializer.deserialize(bytes, messageType);
        list.add(data);
    }
}
