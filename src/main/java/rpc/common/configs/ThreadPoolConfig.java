package rpc.common.configs;

import lombok.Data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Data
public class ThreadPoolConfig {
    /**
     * 线程池默认参数
     */
    private static final int DEFAULT_CORE_POOL_SIZE = 10;
    private static final int DEFAULT_MAXIMUM_POOL_SIZE_SIZE = 100;
    private static final int DEFAULT_KEEP_ALIVE_TIME = 1;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;
    private static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 100;
    public static final int BLOCKING_QUEUE_CAPACITY = 100;
    /**
     * 可配置参数
     */
    public static int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    public static int maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE_SIZE;
    public static long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
    public static TimeUnit unit = DEFAULT_TIME_UNIT;

    static {

        ResourceBundle rb = ResourceBundle.getBundle(RpcConfig.THREAD_POOL_CUSTOM_CONFIG_PATH);
        for (String key : rb.keySet()) {
            String value = rb.getString(key);

            if (key.equals("corePoolSize")) {
                corePoolSize = Integer.parseInt(value);
            } else if (key.equals("maximumPoolSize")) {
                maximumPoolSize = Integer.parseInt(value);
            } else if (key.equals("keepAliveTime")) {
                keepAliveTime = Long.parseLong(value);
            } else if (key.equals("unit")) {
                unit = TimeUnit.valueOf(value);
            }

        }

    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        OutputStream output = null;
        try {
            output = Files.newOutputStream(Paths.get(RpcConfig.THREAD_POOL_CUSTOM_CONFIG_PATH));
            properties.setProperty("corePoolSize", String.valueOf(DEFAULT_CORE_POOL_SIZE));
            properties.setProperty("maximumPoolSize", String.valueOf(DEFAULT_MAXIMUM_POOL_SIZE_SIZE));
            properties.setProperty("keepAliveTime", String.valueOf(DEFAULT_KEEP_ALIVE_TIME));
            properties.setProperty("unit", DEFAULT_TIME_UNIT.toString());

            // 保存键值对到文件中
            properties.store(output, "DEFAULT_CONFIG");

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
