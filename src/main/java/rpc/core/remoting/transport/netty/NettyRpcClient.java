package rpc.core.remoting.transport.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rpc.common.configs.NettyConfig;
import rpc.common.configs.RpcConfig;
import rpc.common.enums.NettyMessageTypeEnum;
import rpc.common.utils.SingletonFactory;
import rpc.core.extension.ExtensionLoader;
import rpc.core.handler.netty.NettyRpcClientHandler;
import rpc.core.registry.ServiceDiscovery;
import rpc.core.remoting.dtos.NettyRpcMessage;
import rpc.core.remoting.dtos.RpcRequest;
import rpc.core.remoting.dtos.RpcResponse;
import rpc.core.remoting.coder.RpcMessageDecoder;
import rpc.core.remoting.coder.RpcMessageEncoder;
import rpc.core.remoting.transport.RpcRequestTransport;
import rpc.core.remoting.transport.netty.utils.ChannelProvider;
import rpc.core.remoting.transport.netty.utils.UnprocessedRequests;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcClient implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;

    public NettyRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline p = socketChannel.pipeline();
                        // 写超时，5秒没有消息写入channel会触发 NettyRpcClientHandler.userEventTriggered 方法，发送一个PING消息
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new NettyRpcClientHandler());
                    }
                });
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension(RpcConfig.serviceDiscovery);
        this.unprocessedRequests = SingletonFactory.getSingleton(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getSingleton(ChannelProvider.class);
    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("Connected to server [{}]", inetSocketAddress);
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException("Completable future isSuccess = [" + future.isSuccess() + "]");
            }
        });
        return completableFuture.get();
    }

    @Override
    public CompletableFuture<RpcResponse<Object>> sendRpcRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        Channel channel = getChannel(inetSocketAddress);

        if (Optional.ofNullable(channel).isPresent() && channel.isActive()) {
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            NettyRpcMessage nettyRpcMessage = NettyRpcMessage.builder()
                    .data(rpcRequest)
                    .compressType(NettyConfig.compressType)
                    .serializeType(NettyConfig.serializerType)
                    .messageType(NettyMessageTypeEnum.REQUEST.getCode())
                    .build();
            channel.writeAndFlush(nettyRpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("Successfully sending message: [{}]", nettyRpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Fail in sending message: [{}]", future.cause().toString());
                }
            });
        } else {
            throw new IllegalStateException("Channel is not active");
        }

        return resultFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (!Optional.ofNullable(channel).isPresent()) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
