package lxt.example.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonSerialization implements Serialization{

    private ObjectMapper objectMapper;

    public JsonSerialization() {
        synchronized(this) {
            objectMapper = new ObjectMapper();
        }
//        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
//        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public <T> byte[] serialize(T obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
