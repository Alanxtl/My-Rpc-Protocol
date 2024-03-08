package rpc.common.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonFactory {
    private static final Map<Class<?>, Object> cacheObject = new ConcurrentHashMap<>();

//    private static SingletonFactory factory = new SingletonFactory();

    public static <T> T getSingleton(Class<T> clazz) {
        if (!Optional.ofNullable(clazz).isPresent()) {
            throw new IllegalArgumentException("Singleton class must not be null");
        }

        T targetObject = (T) cacheObject.get(clazz);
        // 缓存未命中
        if (!Optional.ofNullable(targetObject).isPresent()) {
            try {
                targetObject = clazz.getDeclaredConstructor().newInstance();
                cacheObject.putIfAbsent(clazz, targetObject);
                return targetObject;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        return targetObject;

    }

}
