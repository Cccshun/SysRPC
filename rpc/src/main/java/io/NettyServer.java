package io;

import common.codec.SysDecode;
import common.codec.SysEncode;
import common.message.Request;
import common.message.Response;
import common.serializer.ProtostuffSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import stub.ServiceProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@AllArgsConstructor
public class NettyServer implements RPCServer {
    private ServiceProvider serviceProvider;

    @Override
    public void start(int port) {
        log.info("services start...");
        NioEventLoopGroup boss = new NioEventLoopGroup(3);
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
//                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
//                                    .addLast(new LengthFieldPrepender(4))
//                                    .addLast(new ObjectEncoder())
//                                    .addLast(new ObjectDecoder(Class::forName))
//                                    .addLast(new NettyServerHandler(serviceProvider));
                            pipeline.addLast(new SysDecode())
                                    .addLast(new SysEncode(new ProtostuffSerializer()))
                                    .addLast(new NettyServerHandler(serviceProvider));
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            // 死循环监听
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }

    @AllArgsConstructor
    private static class NettyServerHandler extends SimpleChannelInboundHandler<Request> {
        ServiceProvider serviceProvider;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Request request) {
            Response response = this.getResponse(request);
            ctx.writeAndFlush(response);
            ctx.close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

        private Response getResponse(Request request) {
            Object service = serviceProvider.getService(request.getInterfaceName());
            try {
                Method method = service.getClass().getMethod(request.getMethodName(), request.getParamsType());
                Object result = method.invoke(service, request.getParams());
                return Response.success(result);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return Response.fail(e.toString());
            }
        }
    }
}
