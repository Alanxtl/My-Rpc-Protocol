package lxt.core.serialize;

public interface Serializer {
    // TODO: 序列化框架：
    //  protobuf
    //  protostuff ^
    //  kyro
    //  Hessian
    <T> byte[] serialize(T obj);
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
