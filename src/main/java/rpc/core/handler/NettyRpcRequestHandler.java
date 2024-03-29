package rpc.core.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.channel.ChannelHandlerLifeCycleException;
import rpc.common.configs.NettyConfig;
import rpc.common.enums.NettyMessageTypeEnum;
import rpc.common.enums.RpcExceptionEnum;
import rpc.common.exceptions.RpcException;
import rpc.common.utils.SingletonFactory;
import rpc.core.remoting.dtos.NettyRpcMessage;
import rpc.core.remoting.dtos.RpcRequest;
import rpc.core.remoting.dtos.RpcResponse;

@Slf4j
public class NettyRpcRequestHandler extends SimpleChannelInboundHandler<NettyRpcMessage> {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyRpcRequestHandler() {
        this.rpcRequestHandler = SingletonFactory.getSingleton(RpcRequestHandler.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NettyRpcMessage nettyRpcMessage) {
        log.info("Server receive message: [{}]", nettyRpcMessage);
        byte messageType = nettyRpcMessage.getMessageType();

        NettyRpcMessage msg = NettyRpcMessage.builder()
                .compressType(NettyConfig.compressType)
                .serializeType(NettyConfig.serializerType)
                .messageType(nettyRpcMessage.getMessageType())
                .build();

        if (messageType == NettyMessageTypeEnum.HEARTBEAT_REQUEST.getCode()) {
            msg.setMessageType(NettyMessageTypeEnum.HEARTBEAT_RESPONSE.getCode());
            msg.setData(NettyConfig.PONG);
        } else if ( messageType == NettyMessageTypeEnum.REQUEST.getCode() ) {
            RpcRequest rpcRequest = (RpcRequest) nettyRpcMessage.getData();
            log.info("Server get message [{}]", nettyRpcMessage);
            msg.setMessageType(NettyMessageTypeEnum.RESPONSE.getCode());

            // 交付给rpcRequestHandler进行处理
            Object result = rpcRequestHandler.handle(rpcRequest);

            if ( channelHandlerContext.channel().isActive() && channelHandlerContext.channel().isWritable() ) {
                RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                msg.setData(rpcResponse);
            } else {
                RpcResponse<Throwable> rpcResponse = RpcResponse.fail(new RpcException(RpcExceptionEnum.SERVER_BUSY.getMessage()), rpcRequest.getRequestId());
                msg.setData(rpcResponse);
                log.error("One request have been dropped");
            }
        } else {
            log.error("Incoming message has a illegal message type");
        }

        channelHandlerContext.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
}
