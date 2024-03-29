package rpc.common.configs;

import lombok.Data;

import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

@Data
public class ThreadPoolConfig {

    private static final int DEFAULT_CORE_POOL_SIZE = 10;
    private static final int DEFAULT_MAXIMUM_POOL_SIZE_SIZE = 100;
    private static final int DEFAULT_KEEP_ALIVE_TIME = 1;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;
    private static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 100;
    public static final int BLOCKING_QUEUE_CAPACITY = 100;

    public static int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    public static int maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE_SIZE;
    public static long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
    public static TimeUnit unit = DEFAULT_TIME_UNIT;

    static {

        ResourceBundle rb = ResourceBundle.getBundle("threadPoolConfig");
        for (String key : rb.keySet()) {
            String value = rb.getString(key);

            switch (key) {
                case "corePoolSize":
                    corePoolSize = Integer.parseInt(value);
                    break;
                case "maximumPoolSize":
                    maximumPoolSize = Integer.parseInt(value);
                    break;
                case "keepAliveTime":
                    keepAliveTime = Long.parseLong(value);
                    break;
                case "unit":
                    unit = TimeUnit.valueOf(value);
                    break;
                default:
                    throw new RuntimeException("Unknown config item [" + key + "] in [" + rb.getBaseBundleName() + "].");
            }

        }

    }


}
