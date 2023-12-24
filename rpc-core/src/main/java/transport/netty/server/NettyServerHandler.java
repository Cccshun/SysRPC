package transport.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import protocol.Request;
import protocol.Response;
import stub.ServiceProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final ServiceProvider serviceProvider;
    private long lastActivityTime;

    public NettyServerHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        recordActivityTime();
    }

    /**
     * record last activity time
     */
    private void recordActivityTime() {
        this.lastActivityTime = System.currentTimeMillis();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Request) {
            Response response = this.getResponse((Request) msg);
            ctx.channel().writeAndFlush(response);
        }
        recordActivityTime();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            log.info("the connection is idled for [{}] ms and disconnected..", System.currentTimeMillis() - lastActivityTime);
            ctx.channel().close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

    private Response getResponse(Request request) {
        Object service = serviceProvider.getService(request.getInterfaceName());
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamsType());
            Object result = method.invoke(service, request.getParams());
            if (result instanceof CompletableFuture<?>) {
                return Response.success(((CompletableFuture<?>) result).get(), request.getRequestId());
            } else {
                return Response.success(result, request.getRequestId());
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return Response.fail(e.toString(), request.getRequestId());
        } finally {
            recordActivityTime();
        }
    }
}
