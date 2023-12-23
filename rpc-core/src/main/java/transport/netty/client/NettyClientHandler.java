package transport.netty.client;

import common.exception.RpcError;
import common.exception.RpcException;
import factory.SingletonFactory;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import protocol.HeartBeat;
import protocol.Response;

import java.net.SocketAddress;

@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final RequestsCache requestsCache;
    private static final int MAX_RETRY = 3;
    private int failedTimes = 0;

    public NettyClientHandler() {
        requestsCache = SingletonFactory.getInstance(RequestsCache.class);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().writeAndFlush(new HeartBeat()).addListener((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    failedTimes++;
                    if (failedTimes >= MAX_RETRY) {
                        log.info("retry over [{}] times, disconnected...", failedTimes);
                        throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
                    }
                } else {
                    clearFailedTimes();
                }
            });
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Response) {
            requestsCache.complete((Response) msg);
        }
        clearFailedTimes();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("client catch exception: ", cause);
        cause.printStackTrace();
        ctx.channel().close();
    }

    private void clearFailedTimes() {
        failedTimes = 0;
    }
}
