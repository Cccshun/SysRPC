package codec;

import common.constants.SerializerType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import protocol.SysMessage;
import serialization.Serializer;

public class SysEncode extends MessageToByteEncoder<SysMessage> {
    private final Serializer serializer;

    public SysEncode() {
        this.serializer = Serializer.getSerializer(SerializerType.JDKSERIALIZER);
    }

    public SysEncode(int serialization) {
        this.serializer = Serializer.getSerializer(serialization);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, SysMessage msg, ByteBuf byteBuf) {
        byteBuf.writeInt(SysMessage.MAGIC)
                .writeInt(serializer.getSerializerType())
                .writeInt(msg.getMessageType());

        byte[] bytes = serializer.serialize(msg);
        byteBuf.writeInt(bytes.length)
                .writeBytes(bytes);
    }
}
