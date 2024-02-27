package lxt.example.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;
    private Serialization serialization;

    public RpcDecoder(Class<?> genericClass, Serialization serialization) {
        this.genericClass = genericClass;
        this.serialization = serialization;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if ( byteBuf.readableBytes() < 4 ) {
            return;
        }

        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();

        if ( byteBuf.readableBytes() < dataLength ) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        Object obj = serialization.deserialize(data, genericClass);

        list.add(obj);
    }
}
