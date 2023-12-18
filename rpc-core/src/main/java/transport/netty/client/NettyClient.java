package transport.netty.client;

import codec.SysDecode;
import codec.SysEncode;
import common.constants.SerializerType;
import common.exception.RpcError;
import common.exception.RpcException;
import io.netty.handler.timeout.IdleStateHandler;
import protocol.HeartBeat;
import protocol.Request;
import protocol.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import register.Register;
import register.ZkRegister;
import transport.RPCClient;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient implements RPCClient {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final Register register;

    public NettyClient() {
        register = new ZkRegister();
        eventLoopGroup = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new SysDecode())
                                .addLast(new SysEncode(SerializerType.KRYOSERIALIZER))
                                .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                                .addLast(new ChannelDuplexHandler(){
                                    @Override
                                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                        HeartBeat heartBeat = new HeartBeat();
                                        ctx.writeAndFlush(heartBeat);
                                    }
                                })
                                .addLast(new NettyClientHandler());
                    }
                });
    }

    @Override
    public Response sendRequest(Request request, int serialization) {
        InetSocketAddress address = register.serviceDiscovery(request.getInterfaceName());
        CompletableFuture<Response> responseFuture = new CompletableFuture<>();
        Channel channel;
        try {
            ChannelFuture future = bootstrap.connect(address.getHostName(), address.getPort()).sync();
            channel = future.channel();
            channel.attr(AttributeKey.valueOf("Response")).set(responseFuture);
            channel.writeAndFlush(request).addListener((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    future.channel().close();
                    throw new RpcException(RpcError.REQUEST_SEND_FAIL);
                }
            });
            return responseFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Response.fail(e.toString());
        } finally {
            // active disconnection after completing one call
//            if (channel != null)
//                channel.close();
        }
    }

    @Override
    public void stop() {
        if (!eventLoopGroup.isShutdown()) {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
