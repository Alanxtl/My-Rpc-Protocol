package lxt.example.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Optional;

public class RpcEncoder extends MessageToByteEncoder {
    private Class<?> genericClass;
    private Serialization serialization;

    public RpcEncoder(Class<?> genericClass, Serialization serialization) {
        this.genericClass = genericClass;
        this.serialization = serialization;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf out) throws Exception {
        Optional<Object> optional = Optional.ofNullable(in); // 判空
        if ( optional.isPresent() ) {
            byte[] data = serialization.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
