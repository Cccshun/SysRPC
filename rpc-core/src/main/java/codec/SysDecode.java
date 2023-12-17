package codec;

import common.constants.MessageType;
import protocol.SysMessage;
import serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class SysDecode extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) {
        // if readable bytes less message head, return
        if (byteBuf.readableBytes() < SysMessage.HEADER_LENGTH) {
            return;
        }
        byteBuf.markReaderIndex();

        // decode message header content
        int magic = byteBuf.readInt();
        int serialization = byteBuf.readInt();
        int msgType = byteBuf.readInt();
        int dataLength = byteBuf.readInt();
        if (magic != SysMessage.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }
        if (msgType != MessageType.REQUEST && msgType != MessageType.RESPONSE) {
            return;
        }

        Serializer serializer = Serializer.getSerializer(serialization);

        // deserialize the message body
        byte[] bytes = new byte[dataLength];
        byteBuf.readBytes(bytes);
        SysMessage msg = serializer.deserialize(bytes, msgType);
        list.add(msg);
    }
}
