package rpc.core.handler.netty;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import rpc.common.configs.NettyConfig;
import rpc.common.enums.NettyMessageTypeEnum;
import rpc.common.enums.RpcExceptionEnum;
import rpc.common.exceptions.RpcException;
import rpc.common.utils.SingletonFactory;
import rpc.core.handler.RpcRequestHandler;
import rpc.core.remoting.dtos.NettyRpcMessage;
import rpc.core.remoting.dtos.RpcRequest;
import rpc.core.remoting.dtos.RpcResponse;

@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyRpcServerHandler() {
        this.rpcRequestHandler = SingletonFactory.getSingleton(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg0) {
        try {
            if ( msg0 instanceof NettyRpcMessage ) {
                NettyRpcMessage nettyRpcMessage = (NettyRpcMessage) msg0;
                log.info("Server receive message: [{}]", nettyRpcMessage);
                byte messageType = nettyRpcMessage.getMessageType();

                NettyRpcMessage msg = NettyRpcMessage.builder()
                        .compressType(NettyConfig.compressType)
                        .serializeType(NettyConfig.serializerType)
                        .messageType(messageType)
                        .build();

                if (messageType == NettyMessageTypeEnum.HEARTBEAT_REQUEST.getCode()) {
                    msg.setMessageType(NettyMessageTypeEnum.HEARTBEAT_RESPONSE.getCode());
                    msg.setData(NettyConfig.PONG);
                } else if (messageType == NettyMessageTypeEnum.REQUEST.getCode()) {
                    RpcRequest rpcRequest = (RpcRequest) nettyRpcMessage.getData();
                    log.info("Server get message [{}]", nettyRpcMessage);
                    msg.setMessageType(NettyMessageTypeEnum.RESPONSE.getCode());

                    // 交付给rpcRequestHandler进行处理
                    Object result = rpcRequestHandler.handle(rpcRequest);

                    if (channelHandlerContext.channel().isActive() && channelHandlerContext.channel().isWritable()) {
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
        } finally {
            ReferenceCountUtil.release(msg0);
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if ( evt instanceof IdleStateEvent ) {
            IdleState state = ((IdleStateEvent) evt).state();
            if ( state == IdleState.READER_IDLE ) {
                log.info("Idle check happened, no heart beat received, the connection will be closed");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Server caught exception");
        ctx.close();
    }
}
