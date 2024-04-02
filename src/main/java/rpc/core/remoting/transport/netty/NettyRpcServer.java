package rpc.core.remoting.transport.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rpc.common.configs.RpcConfig;
import rpc.common.utils.SingletonFactory;
import rpc.common.utils.ThreadPoolUtil;
import rpc.core.handler.NettyRpcRequestHandler;
import rpc.core.provider.TargetRpcService;
import rpc.core.provider.ServiceProvider;
import rpc.core.provider.zk.ZkServiceProvider;
import rpc.common.utils.zkShutdownHook;
import rpc.core.remoting.transport.netty.coder.RpcMessageDecoder;
import rpc.core.remoting.transport.netty.coder.RpcMessageEncoder;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcServer {
    private final ServiceProvider serviceProvider = SingletonFactory.getSingleton(ZkServiceProvider.class);

    public void registerService(TargetRpcService rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    @SneakyThrows
    public void start() {
        zkShutdownHook zkShutdownHook = SingletonFactory.getSingleton(zkShutdownHook.class);
        zkShutdownHook.clearAll();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                Runtime.getRuntime().availableProcessors() * 2,
                ThreadPoolUtil.createThreadFactory("service-handler-group", false)
        );

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sh) {
                            ChannelPipeline p = sh.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(serviceHandlerGroup, new NettyRpcRequestHandler());
                        }
                    });
            ChannelFuture f = b.bind(InetAddress.getLocalHost().getHostAddress(), RpcConfig.rpcServerPort).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("A error occurred when starting netty server");
        } finally {
            log.error("Shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }

}
