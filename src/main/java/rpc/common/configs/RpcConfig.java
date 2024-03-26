package rpc.common.configs;

import lombok.Data;
import rpc.common.enums.extensionEnums.SerializerExtensionEnum;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;

@Data
public class RpcConfig {
    public static final String RPC_CUSTOM_CONFIG_PATH = "src/main/resources/rpcConfig.properties";
    public static final String ZK_CUSTOM_CONFIG_PATH = "src/main/resources/zookeeperConfig.properties";
    public static final String THREAD_POOL_CUSTOM_CONFIG_PATH = "src/main/resources/threadPoolConfig.properties";
    private static final int DEFAULT_RPC_SERVER_PORT = 9998;
    private static final String DEFAULT_SERIALIZER = SerializerExtensionEnum.PROTOSTUFF.serviceName;

    public static int rpcServerPort = DEFAULT_RPC_SERVER_PORT;
    public static String serializer = DEFAULT_SERIALIZER;

    static {

        ResourceBundle rb = ResourceBundle.getBundle(RPC_CUSTOM_CONFIG_PATH);
        for (String key : rb.keySet()) {
            String value = rb.getString(key);

            if ( key.equals("SERIALIZER") ) {
                serializer = value;
            } else if ( key.equals("RPC_SERVER_PORT") ) {
                rpcServerPort = Integer.parseInt(value);
            }

        }

    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        OutputStream output = null;
        try {
            output = Files.newOutputStream(Paths.get(RPC_CUSTOM_CONFIG_PATH));
            properties.setProperty("SERIALIZER", SerializerExtensionEnum.PROTOSTUFF.serviceName);
            properties.setProperty("RPC_SERVER_PORT", "9998");

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
