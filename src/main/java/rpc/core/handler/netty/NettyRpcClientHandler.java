package rpc.core.handler.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import rpc.common.configs.NettyConfig;
import rpc.common.enums.NettyMessageTypeEnum;
import rpc.common.utils.SingletonFactory;
import rpc.core.remoting.dtos.NettyRpcMessage;
import rpc.core.remoting.dtos.RpcResponse;
import rpc.core.remoting.transport.netty.NettyRpcClient;
import rpc.core.remoting.transport.netty.utils.UnprocessedRequests;

import java.net.InetSocketAddress;

@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<NettyRpcMessage> {
    private final UnprocessedRequests unprocessedRequests;
    private final NettyRpcClient nettyRpcClient;

    public NettyRpcClientHandler() {
        this.unprocessedRequests = SingletonFactory.getSingleton(UnprocessedRequests.class);
        this.nettyRpcClient = SingletonFactory.getSingleton(NettyRpcClient.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NettyRpcMessage nettyRpcMessage) throws Exception {
        log.info("Client receive message: [{}]", nettyRpcMessage);
        byte messageType = nettyRpcMessage.getMessageType();
        if ( messageType == NettyMessageTypeEnum.HEARTBEAT_RESPONSE.getCode() ) {
            log.info("Heart beat [{}]", nettyRpcMessage.getData());
        } else if ( messageType == NettyMessageTypeEnum.RESPONSE.getCode() ) {
            RpcResponse<Object> rpcResponse = (RpcResponse<Object>) nettyRpcMessage.getData();
            unprocessedRequests.complete(rpcResponse);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if ( evt instanceof IdleStateEvent ) {
            IdleState state = ((IdleStateEvent) evt).state();
            if ( state == IdleState.WRITER_IDLE ) {
                log.info("Idle written happened [{}]", ctx.channel().remoteAddress());
                Channel channel = nettyRpcClient.getChannel((InetSocketAddress) ctx.channel().remoteAddress());
                NettyRpcMessage nettyRpcMessage = NettyRpcMessage.builder()
                        .serializeType(NettyConfig.serializerType)
                        .compressType(NettyConfig.compressType)
                        .messageType(NettyMessageTypeEnum.HEARTBEAT_REQUEST.getCode())
                        .data(NettyConfig.PING)
                        .build();
                channel.writeAndFlush(nettyRpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Client caught an error", cause);
        ctx.close();
    }


}
