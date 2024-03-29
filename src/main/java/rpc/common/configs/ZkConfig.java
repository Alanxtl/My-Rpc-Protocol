package rpc.common.configs;

import lombok.Data;
import rpc.common.enums.extensionEnums.LoadBalanceExtensionEnum;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Data
public class ZkConfig {

    private static final String DEFAULT_ZK_ADDRESS = "127.0.0.1:2181";
    private static final String DEFAULT_LOAD_BALANCE = LoadBalanceExtensionEnum.CONSISTENT_HASH_LOAD_BALANCE.serviceName;

    public static final int BASE_SLEEP_TIME = 1000;
    public static final int MAX_RETRIES = 3;
    public static final String ZK_REGISTER_ROOT_PATH = "/rpc";

    public static String zkAddress = DEFAULT_ZK_ADDRESS;
    public static String loadBalance = DEFAULT_LOAD_BALANCE;

    static {
        try {
            ResourceBundle rb = ResourceBundle.getBundle("zookeeperConfig");
            for (String key : rb.keySet()) {
                String value = rb.getString(key);

                switch (key) {
                    case "zkAddress":
                        zkAddress = value;
                        break;
                    case "zkServiceProvider":
                        RpcConfig.serviceProvider = value;
                        break;
                    case "zkServiceDiscovery":
                        RpcConfig.serviceDiscovery = value;
                        break;
                    case "loadBalance":
                        loadBalance = value;
                        break;
                    default:
                        throw new RuntimeException("Unknown config item [" + key + "] in [" + rb.getBaseBundleName() + "].");
                }
            }
        } catch (MissingResourceException ignored) {}
    }

}