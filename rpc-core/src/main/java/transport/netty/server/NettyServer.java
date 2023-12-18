package transport.netty.server;

import codec.SysDecode;
import codec.SysEncode;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import stub.ServiceProvider;
import transport.RPCServer;

import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
public class NettyServer implements RPCServer {
    private ServiceProvider serviceProvider;

    @Override
    public void start(int port) {
        log.info("services start...");
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup(2);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new SysDecode())
                                    .addLast(new SysEncode())
                                    .addLast(new IdleStateHandler(15, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new NettyServerHandler(serviceProvider));
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            // 死循环监听
            future.channel().closeFuture().sync();
            System.out.println(future);
            log.info(Thread.currentThread().toString());
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
}
