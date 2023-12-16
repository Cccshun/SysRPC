package common.codec;

import common.constants.MessageType;
import common.message.Request;
import common.message.Response;
import common.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SysEncode extends MessageToByteEncoder {
    private Serializer serializer;
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf byteBuf) {
        log.info(msg.getClass().toString());
        // encode message type into message with 2 byte
        if (msg instanceof Request) {
            byteBuf.writeShort(MessageType.REQUEST);
        } else if (msg instanceof Response) {
            byteBuf.writeShort(MessageType.RESPONSE);
        }

        // encode serializer type into message with 2 byte
        byteBuf.writeShort(serializer.getSerializerType());

        byte[] bytes = serializer.serialize(msg);
        // encode data length into message with 4 byte
        byteBuf.writeInt(bytes.length);
        // encode data into message
        byteBuf.writeBytes(bytes);
        log.info(byteBuf.toString());
    }
}
