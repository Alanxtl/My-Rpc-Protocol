package rpc.core.remoting.transport.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import rpc.common.enums.CompressTypeEnum;
import rpc.common.enums.NettyMessageTypeEnum;
import rpc.core.extension.ExtensionLoader;
import rpc.core.compress.Compress;
import rpc.core.remoting.dtos.NettyRpcMessage;
import rpc.core.remoting.transport.netty.NettyRpcConstants;

import java.util.Arrays;

@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        this(NettyRpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }



    /**
     * @param maxFrameLength      最大帧长度，超出部分将被丢弃。
     * @param lengthFieldOffset   长度域偏移量（长度域之前内容的长度）
     * @param lengthFieldLength   长度域长度.
     * @param lengthAdjustment    回到最开始
     * @param initialBytesToStrip 跳过头部的长度。
     *                            若需要接收头部该值设为0。
     *                            若只需要接受正文部分，则该值设为头部长度。
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx,in);
        if ( decoded instanceof ByteBuf ) {
            ByteBuf frame = (ByteBuf) decoded;
            if ( frame.readableBytes() >= NettyRpcConstants.TOTAL_LENGTH ) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error!", e);
                    throw e;
                }
                finally {
                    frame.release();
                }
            }
        }

        return null;

    }

    private Object decodeFrame(ByteBuf in) {
        checkMagicNumber(in);
        checkVersion(in);

        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();

        NettyRpcMessage rpcMessage = NettyRpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType)
                .build();

        if (messageType == NettyMessageTypeEnum.HEARTBEAT_REQUEST.getCode()) {
            rpcMessage.setData(NettyRpcConstants.PING);
            return rpcMessage;
        }

        if (messageType == NettyMessageTypeEnum.HEARTBEAT_RESPONSE.getCode()) {
            rpcMessage.setData(NettyRpcConstants.PONG);
            return rpcMessage;
        }

        int bodyLength = fullLength - NettyRpcConstants.HEAD_LENGTH;

        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);

            String compressName = CompressTypeEnum.getEnum(compressType);

            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            bs = compress.decompress(bs);
        }

        return null;
    }

    private void checkVersion(ByteBuf in) {
        // 读入版本号并比较
        byte version = in.readByte();
        if (version != NettyRpcConstants.VERSION) {
            throw new RuntimeException("Version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        // 读入魔数并比较
        int len = NettyRpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != NettyRpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }

}
