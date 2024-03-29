package rpc.core.remoting.transport.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import rpc.common.configs.NettyConfig;
import rpc.common.enums.NettyMessageTypeEnum;
import rpc.common.enums.extensionEnums.CompressExtensionEnum;
import rpc.common.enums.extensionEnums.SerializerExtensionEnum;
import rpc.core.compress.Compress;
import rpc.core.extension.ExtensionLoader;
import rpc.core.remoting.dtos.NettyRpcMessage;
import rpc.core.serialize.Serializer;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * <p>
 * Custom Protocol Encoder
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
 *   body（Object类型数据，先序列化再压缩）
 * </pre>
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<NettyRpcMessage> {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyRpcMessage nettyRpcMessage, ByteBuf out) {
        try {
            out.writeBytes(NettyConfig.MAGIC_NUMBER);
            out.writeByte(NettyConfig.VERSION);
            out.writerIndex(out.writerIndex() + 4);

            byte messageType = nettyRpcMessage.getMessageType();
            out.writeByte(messageType);
            out.writeByte(nettyRpcMessage.getSerializeType());
            out.writeByte(NettyConfig.compressType);
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());

            byte[] bodyBytes = null;
            int fullLength = NettyConfig.HEAD_LENGTH;

            if ( messageType != NettyMessageTypeEnum.HEARTBEAT_RESPONSE.getCode() && messageType != NettyMessageTypeEnum.HEARTBEAT_REQUEST.getCode()) {

                String codecName = SerializerExtensionEnum.getEnum(nettyRpcMessage.getSerializeType());
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
                bodyBytes = serializer.serialize(nettyRpcMessage.getData());

                String compressName = CompressExtensionEnum.getEnum(nettyRpcMessage.getCompressType());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);

                log.info("Serializer: [{}] Compress: [{}]", codecName, compressName);

                fullLength += bodyBytes.length;
            }

            if ( bodyBytes != null ) {
                out.writeBytes(bodyBytes);
            }

            int writerIndex = out.writerIndex();

            out.writerIndex(writerIndex - fullLength + NettyConfig.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writerIndex);

        } catch (Exception e) {
            log.error("Encode request error", e);
        }
    }
}
