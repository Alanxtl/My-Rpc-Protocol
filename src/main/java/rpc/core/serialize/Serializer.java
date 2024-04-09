package rpc.core.serialize;

import rpc.core.extension.SPI;

@SPI(messageType = "Serializer")
public interface Serializer {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
