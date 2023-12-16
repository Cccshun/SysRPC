package io;

import common.codec.SysDecode;
import common.codec.SysEncode;
import common.message.Request;
import common.message.Response;
import common.serializer.ProtostuffSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import register.Register;
import register.ZkRegister;

import java.net.InetSocketAddress;

@Slf4j
public class NettyClient implements RPCClient {

    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private final Register register;

    public NettyClient() {
        register = new ZkRegister();
    }

    static {
        eventLoopGroup = new NioEventLoopGroup(2);
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        /*pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                .addLast(new LengthFieldPrepender(4))
                                .addLast(new ObjectEncoder())
                                .addLast(new ObjectDecoder(Class::forName))
                                .addLast(new NettyClientHandler());*/
                        pipeline.addLast(new SysDecode())
                                .addLast(new SysEncode(new ProtostuffSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });
    }

    @Override
    public Response sendRequest(Request request) {
        InetSocketAddress address = register.serviceDiscovery(request.getInterfaceName());
        Channel channel = null;
        try {
            ChannelFuture future = bootstrap.connect(address.getHostName(), address.getPort()).sync();
            channel = future.channel();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
            AttributeKey<Response> key = AttributeKey.valueOf("Response");
            return channel.attr(key).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Response.fail(e.toString());
        } finally {
            if (channel != null)
                channel.close();
        }
    }

    private static class NettyClientHandler extends SimpleChannelInboundHandler<Response> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Response msg) {
            AttributeKey<Response> key = AttributeKey.valueOf("Response");
            ctx.channel().attr(key).set(msg);
            ctx.close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
