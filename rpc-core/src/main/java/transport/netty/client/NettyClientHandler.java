package transport.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import protocol.Response;

import java.util.concurrent.CompletableFuture;

public class NettyClientHandler extends SimpleChannelInboundHandler<Response> {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response msg) {
        CompletableFuture<Response> future = (CompletableFuture<Response>) ctx.channel().attr(AttributeKey.valueOf("Response")).get();
        future.complete(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
