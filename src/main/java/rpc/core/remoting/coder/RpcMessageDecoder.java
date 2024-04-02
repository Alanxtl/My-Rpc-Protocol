package rpc.core.remoting.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import rpc.common.enums.NettyMessageTypeEnum;
import rpc.common.enums.extensionEnums.CompressExtensionEnum;
import rpc.common.enums.extensionEnums.SerializerExtensionEnum;
import rpc.core.compress.Compress;
import rpc.core.extension.ExtensionLoader;
import rpc.core.remoting.dtos.NettyRpcMessage;
import rpc.common.configs.NettyConfig;
import rpc.core.remoting.dtos.RpcRequest;
import rpc.core.remoting.dtos.RpcResponse;
import rpc.core.serialize.Serializer;

import java.util.Arrays;

/**
 * <p>
 * Custom Protocol Decoder
 * <p>
 * <pre>
 *   0     1     2     3     4        5      6     7     8     9          10      11       12   13   14   15   16
 *   +-----+-----+-----+-----+---------+-----+-----+-----+-----+-----------+-------+--------+----+----+----+----+
 *   |     magic   code      | version |     full   length     |messageType| codec |compress|    RequestId      |
 *   +-----------------------+---------+-----------------------+-----------+-------+--------+-------------------+
 *   |                                                                                                          |
 *   |                                          body                                                            |
 *   |                                                                                                          |
 *   |                                         ... ...                                                          |
 *   +----------------------------------------------------------------------------------------------------------+
 *   4B magic code（魔数）  1B version（版本）     4B full length（消息长度） 1B messageType（消息类型）
 *   1B compress（压缩类型） 1B codec（序列化类型）  4B  requestId（请求的Id）
 *   body（Object类型数据，先解压缩再解序列化）
 * </pre>
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        this(NettyConfig.MAX_FRAME_LENGTH, 5, 4, -9, 0);
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
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= NettyConfig.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error!", e);
                    throw e;
                } finally {
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
                .serializeType(codecType)
                .requestId(requestId)
                .messageType(messageType)
                .build();

        if (messageType == NettyMessageTypeEnum.HEARTBEAT_REQUEST.getCode()) {
            rpcMessage.setData(NettyConfig.PING);
            return rpcMessage;
        }

        if (messageType == NettyMessageTypeEnum.HEARTBEAT_RESPONSE.getCode()) {
            rpcMessage.setData(NettyConfig.PONG);
            return rpcMessage;
        }

        int bodyLength = fullLength - NettyConfig.HEAD_LENGTH;

        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);

            String compressName = CompressExtensionEnum.getEnum(rpcMessage.getCompressType());
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            bs = compress.decompress(bs);

            String codecName = SerializerExtensionEnum.getEnum(rpcMessage.getSerializeType());
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);

            if ( messageType == NettyMessageTypeEnum.REQUEST.getCode() ) {
                RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(tmpValue);
            }

            if ( messageType == NettyMessageTypeEnum.RESPONSE.getCode() ) {
                RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }

            log.info("Serializer: [{}] Compress: [{}]", codecName, compressName);

        } else if ( bodyLength < 0 ) {
            log.error("Incoming message body decode error.");
        }

        return rpcMessage;
    }

    private void checkVersion(ByteBuf in) {
        // 读入版本号并比较
        byte version = in.readByte();
        if (version != NettyConfig.VERSION) {
            throw new IllegalArgumentException("Version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        // 读入魔数并比较
        int len = NettyConfig.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != NettyConfig.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }

}
