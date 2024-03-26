package rpc.common.configs;

import lombok.Data;
import rpc.common.enums.extensionEnums.ServiceDiscoveryExtensionEnum;
import rpc.common.enums.extensionEnums.ServiceRegistryExtensionEnum;
import rpc.common.enums.extensionEnums.LoadBalanceExtensionEnum;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;

@Data
public class ZkConfig {
    public static final int BASE_SLEEP_TIME = 1000;
    public static final int MAX_RETRIES = 3;
    public static final String ZK_REGISTER_ROOT_PATH = "/rpc";
    private static final String DEFAULT_ZK_ADDRESS = "127.0.0.1:2181";
    private static final String DEFAULT_ZK_SERVICE_PROVIDER = ServiceRegistryExtensionEnum.ZK.serviceName;
    private static final String DEFAULT_ZK_SERVICE_DISCOVERY = ServiceDiscoveryExtensionEnum.ZK.serviceName;
    private static final String DEFAULT_LOAD_BALANCE = LoadBalanceExtensionEnum.CONSISTENT_HASH_LOAD_BALANCE.serviceName;

    public static String zkAddress = DEFAULT_ZK_ADDRESS;
    public static String zkServiceProvider = DEFAULT_ZK_SERVICE_PROVIDER;
    public static String zkServiceDiscovery = DEFAULT_ZK_SERVICE_DISCOVERY;
    public static String loadBalance = DEFAULT_LOAD_BALANCE;

    static {

        ResourceBundle rb = ResourceBundle.getBundle(RpcConfig.ZK_CUSTOM_CONFIG_PATH);
        for (String key : rb.keySet()) {
            String value = rb.getString(key);

            if (key.equals("zkAddress")) {
                zkAddress = value;
            } else if (key.equals("zkServiceProvider")) {
                zkServiceProvider = value;
            } else if (key.equals("zkServiceDiscovery")) {
                zkServiceDiscovery = value;
            } else if (key.equals("loadBalance")) {
                loadBalance = value;
            }

        }

    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        OutputStream output = null;
        try {
            output = Files.newOutputStream(Paths.get(RpcConfig.ZK_CUSTOM_CONFIG_PATH));
            properties.setProperty("ZK_ADDRESS", "127.0.0.1:2181");
            properties.setProperty("ZK_SERVICE_PROVIDER", ServiceRegistryExtensionEnum.ZK.serviceName);
            properties.setProperty("ZK_SERVICE_DISCOVERY", ServiceDiscoveryExtensionEnum.ZK.serviceName);
            properties.setProperty("LOAD_BALANCE", LoadBalanceExtensionEnum.CONSISTENT_HASH_LOAD_BALANCE.serviceName);

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