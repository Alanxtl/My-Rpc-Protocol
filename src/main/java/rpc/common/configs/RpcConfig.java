package rpc.common.configs;

import lombok.Data;
import rpc.common.enums.extensionEnums.SerializerExtensionEnum;

import java.util.ResourceBundle;

@Data
public class RpcConfig {
    private static final String ZK_CUSTOM_CONFIG_PATH = "src/main/resources/zookeeperConfig.properties";
    private static final String NETTY_CUSTOM_CONFIG_PATH = "src/main/resources/zookeeperConfig.properties";
    private static final String THREAD_POOL_CUSTOM_CONFIG_PATH = "src/main/resources/threadPoolConfig.properties";
    private static final int DEFAULT_RPC_SERVER_PORT = 9998;
    private static final byte DEFAULT_SERIALIZER_TYPE = SerializerExtensionEnum.PROTOSTUFF.code;

    public static int rpcServerPort = DEFAULT_RPC_SERVER_PORT;
    public static byte serializerType = DEFAULT_SERIALIZER_TYPE;

    static {
        ResourceBundle rb = ResourceBundle.getBundle("rpcConfig");
        for (String key : rb.keySet()) {
            String value = rb.getString(key);

            switch (key) {
                case "serializerType":
                    serializerType = Byte.parseByte(value);
                    break;
                case "rpcServerPort":
                    rpcServerPort = Integer.parseInt(value);
                    break;
                default:
                    throw new RuntimeException("Unknown config item [" + key + "] in [" + rb.getBaseBundleName() + "].");
            }

        }

    }

}
