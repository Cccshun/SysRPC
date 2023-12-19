package transport.netty.client;

import factory.SingletonFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import protocol.HeartBeat;
import protocol.Response;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<Response> {
    private final RequestsCache requestsCache;

    public NettyClientHandler() {
        requestsCache = SingletonFactory.getInstance(RequestsCache.class);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            // long time no write
            if (state == IdleState.WRITER_IDLE) {
                ctx.channel().writeAndFlush(new HeartBeat()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response msg) {
        requestsCache.complete(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("client catch exception: ", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
