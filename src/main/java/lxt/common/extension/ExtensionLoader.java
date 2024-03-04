package lxt.common.extension;

import lombok.extern.slf4j.Slf4j;

import javax.xml.ws.Holder;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public final class ExtensionLoader<T> {
    private static final String FULL_SERVICE_DIRECTORY = "src/main/resources/META-INF/extensions/";
    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();
    private static final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    private static final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    static {
        synchronized (cachedClasses) { // double check
            Map<String, Class<?>> classes = cachedClasses.value;
            if (!Optional.ofNullable(classes).isPresent()) {
                classes = new HashMap<>();
                cachedClasses.value = classes;
            }
        }

        // 加载默认资源目录
        try {
            loadDefaultResources();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load default directories", e);
        }
    }

    private final Class<?> type;

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        if (!Optional.ofNullable(type).isPresent()) {
            throw new IllegalArgumentException("Extension type should not be null");
        } else if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type should be an interface");
        } else if (!Optional.ofNullable(type.getAnnotation(SPI.class)).isPresent()) {
            throw new IllegalArgumentException("Extension type should be annotated by @SPI");
        }

        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        // 缓存未命中
        if (!Optional.ofNullable(extensionLoader).isPresent()) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }

        return extensionLoader;
    }

    private static void loadDefaultResources() throws IOException {
        // 加载默认资源
        ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
        File folder = new File(ExtensionLoader.FULL_SERVICE_DIRECTORY);
        File[] files = folder.listFiles();

        if (Optional.ofNullable(files).isPresent()) {
            for (File file : files) {
                String fileName = SERVICE_DIRECTORY + file.getName();
                Enumeration<URL> urls = classLoader.getResources(fileName);

                if (urls != null) {
                    while (urls.hasMoreElements()) {
                        URL resourceUrl = urls.nextElement();
                        loadResource(cachedClasses.value, classLoader, resourceUrl);
                    }
                }
            }
        }
    }

    private static void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {
            // 逐行加载
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.contains("#")) {
                    // 忽略注释
                    line = line.substring(0, line.indexOf('#'));
                }

                line = line.trim();
                if (!line.isEmpty()) {
                    final int ei = line.indexOf('=');
                    String name = line.substring(0, ei).trim();
                    String clazzName = line.substring(ei + 1).trim();

                    try {
                        // 将等号左右两边插入缓存
                        if (!name.isEmpty() && !clazzName.isEmpty()) {
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            extensionClasses.put(name, clazz);
                            log.info("Load class: " + name + " Class name: " + clazzName);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error("Unable to find the class " + clazzName + "\n Caused by: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Unable to read the file " + resourceUrl + "\n Caused by: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ExtensionLoader extensionLoader = new ExtensionLoader(ExtensionLoader.class);
        System.out.println(ExtensionLoader.cachedClasses.value);
    }

    public T getExtension(String name) {
        if (!Optional.ofNullable(name).isPresent() || name.isEmpty()) {
            throw new IllegalArgumentException("Extension name should not be null or empty");
        }

        Holder<Object> holder = cachedInstances.get(name);
        // 缓存未命中
        if (!Optional.ofNullable(holder).isPresent()) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }

        Object instance = holder.value;
        // 创建单例
        if (!Optional.ofNullable(instance).isPresent()) {
            synchronized (holder) {
                instance = holder.value;
                if (!Optional.ofNullable(instance).isPresent()) {
                    instance = createExtension(name);
                    holder.value = instance;
                }
            }
        }

        return (T) instance;
    }

    private T createExtension(String name) {
        // 加载所有的T类 并使用name返回指定的类
        Class<?> clazz = getExtensionClasses().get(name);
        if (!Optional.ofNullable(clazz).isPresent()) {
            throw new IllegalStateException("No such extension of name " + name);
        }

        T instance = (T) EXTENSION_INSTANCES.get(clazz);

        if (!Optional.ofNullable(instance).isPresent()) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error("Fail to create instance of extension " + clazz + "\n Caused by: " + e.getMessage());
            }
        }

        return instance;
    }

    private Map<String, Class<?>> getExtensionClasses() {
        // 从缓存中加载
        Map<String, Class<?>> classes = cachedClasses.value;
        if (!Optional.ofNullable(classes).isPresent()) {
            synchronized (cachedClasses) { // double check
                classes = cachedClasses.value;
                if (!Optional.ofNullable(classes).isPresent()) {
                    classes = new HashMap<>();
                    // 加载所有的T类
                    loadDirectory(classes);
                    cachedClasses.value = classes;
                }
            }
        }

        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            log.error("Unable to locate the file " + fileName + "\n Caused by: " + e.getMessage());
        }
    }

}
