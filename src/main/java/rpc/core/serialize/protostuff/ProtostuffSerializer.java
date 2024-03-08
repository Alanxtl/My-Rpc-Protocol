package rpc.core.serialize.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import rpc.core.serialize.Serializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffSerializer implements Serializer {

    private static final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    private static final Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    @Override
    public <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        Schema<T> schema = getSchema(clazz);
        byte[] out = null;
        try {
            out = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            buffer.clear();
        }

        return out;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        T out = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, out, schema);

        return out;
    }

    private static <T> Schema<T> getSchema(Class<T> clazz) {

        Schema<T> schema = null;

        if (schemaCache.containsKey(clazz)) {
            schema = (Schema<T>) schemaCache.get(clazz);
        } else {
            schema = RuntimeSchema.getSchema(clazz);
            schemaCache.put(clazz, schema);
        }

        return schema;
    }

    public static void main(String[] args) {

        ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();
        Integer str = 123;
        byte[] bytes = protostuffSerializer.serialize(str);
        System.out.println(protostuffSerializer.deserialize(protostuffSerializer.serialize(str), Integer.class));

    }
}
