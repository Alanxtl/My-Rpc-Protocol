package rpc.core.serialize;

import rpc.core.extension.SPI;

@SPI
public interface Serializer {
    // TODO: 序列化框架：
    //  protobuf
    //  protostuff ^
    //  kyro
    //  Hessian
    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
