package lxt.example.protocol;

public interface Serialization {
    // TODO: 序列化框架：protobuf kyro Hessian
    <T> byte[] serialize(T obj);
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
