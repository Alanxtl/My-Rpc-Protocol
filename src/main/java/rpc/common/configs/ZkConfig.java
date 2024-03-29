package rpc.common.configs;

import lombok.Data;
import rpc.common.enums.extensionEnums.LoadBalanceExtensionEnum;
import rpc.common.enums.extensionEnums.ServiceDiscoveryExtensionEnum;
import rpc.common.enums.extensionEnums.ServiceRegistryExtensionEnum;

import java.util.ResourceBundle;

@Data
public class ZkConfig {

    private static final String DEFAULT_ZK_ADDRESS = "127.0.0.1:2181";
    private static final String DEFAULT_ZK_SERVICE_PROVIDER = ServiceRegistryExtensionEnum.ZK.serviceName;
    private static final String DEFAULT_ZK_SERVICE_DISCOVERY = ServiceDiscoveryExtensionEnum.ZK.serviceName;
    private static final String DEFAULT_LOAD_BALANCE = LoadBalanceExtensionEnum.CONSISTENT_HASH_LOAD_BALANCE.serviceName;

    public static final int BASE_SLEEP_TIME = 1000;
    public static final int MAX_RETRIES = 3;
    public static final String ZK_REGISTER_ROOT_PATH = "/rpc";

    public static String zkAddress = DEFAULT_ZK_ADDRESS;
    public static String zkServiceProvider = DEFAULT_ZK_SERVICE_PROVIDER;
    public static String zkServiceDiscovery = DEFAULT_ZK_SERVICE_DISCOVERY;
    public static String loadBalance = DEFAULT_LOAD_BALANCE;

    static {

        ResourceBundle rb = ResourceBundle.getBundle("zookeeperConfig");
        for (String key : rb.keySet()) {
            String value = rb.getString(key);

            switch (key) {
                case "zkAddress":
                    zkAddress = value;
                    break;
                case "zkServiceProvider":
                    zkServiceProvider = value;
                    break;
                case "zkServiceDiscovery":
                    zkServiceDiscovery = value;
                    break;
                case "loadBalance":
                    loadBalance = value;
                    break;
                default:
                    throw new RuntimeException("Unknown config item [" + key + "] in [" + rb.getBaseBundleName() + "].");
            }

        }

    }

}