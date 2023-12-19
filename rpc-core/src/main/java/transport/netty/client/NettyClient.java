package transport.netty.client;

import codec.SysDecode;
import codec.SysEncode;
import common.constants.SerializerType;
import common.exception.RpcError;
import common.exception.RpcException;
import factory.SingletonFactory;
import io.netty.handler.timeout.IdleStateHandler;
import protocol.HeartBeat;
import protocol.Request;
import protocol.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
    private final RequestsCache requestsCache;
    private final ChannelCache channelsCache;

    public NettyClient() {
        register = new ZkRegister();
        eventLoopGroup = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //  If this time is exceeded or the connection cannot be established, the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new SysDecode())
                                .addLast(new SysEncode(SerializerType.KRYOSERIALIZER))
                                .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                                .addLast(new NettyClientHandler());
                    }
                });

        this.requestsCache = SingletonFactory.getInstance(RequestsCache.class);
        this.channelsCache = SingletonFactory.getInstance(ChannelCache.class);
    }

    @Override
    public Object sendRequest(Request request, int serialization) throws RuntimeException {
        InetSocketAddress address = register.serviceDiscovery(request.getInterfaceName());
        CompletableFuture<Response> responseFuture = new CompletableFuture<>();
        requestsCache.put(request.getRequestId(), responseFuture);
        Channel channel = getChannel(address);
        if (channel.isActive()) {
            channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message [{}]", request);
                } else {
                    channel.close();
                    responseFuture.completeExceptionally(future.cause());
                    throw new RpcException(RpcError.REQUEST_SEND_FAIL);
                }
            });
        } else {
            log.info("channel is disconnected [{}]", channel);
            throw new IllegalStateException();
        }
        return responseFuture;
    }

    /**
     * establish connection and get the channel
     */
    public Channel doConnect(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    /**
     * get channel by service provider address
     */
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelsCache.get(inetSocketAddress);
        if (channel == null) {
            try {
                channel = doConnect(inetSocketAddress);
                channelsCache.set(inetSocketAddress, channel);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return channel;
    }

    @Override
    public void stop() {
        if (!eventLoopGroup.isShutdown()) {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
