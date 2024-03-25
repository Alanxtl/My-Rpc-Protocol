package rpc.common.configs;

import lombok.Data;
import rpc.common.enums.extensionEnums.ServiceDiscoveryExtensionEnum;
import rpc.common.enums.extensionEnums.ServiceRegistryExtensionEnum;
import rpc.common.enums.extensionEnums.LoadBalanceExtensionEnum;

@Data
public class ZkConfig {
    public static final int BASE_SLEEP_TIME = 1000;
    public static final int MAX_RETRIES = 3;
    public static final String ZK_REGISTER_ROOT_PATH = "/rpc";
    public static String DEFAULT_ZK_ADDRESS = "127.0.0.1:2181";
    public static String ZK_SERVICE_PROVIDER = ServiceRegistryExtensionEnum.ZK.serviceName;
    public static String ZK_SERVICE_DISCOVERY = ServiceDiscoveryExtensionEnum.ZK.serviceName;
    public static String LOAD_BALANCE = LoadBalanceExtensionEnum.CONSISTENT_HASH_LOAD_BALANCE.serviceName;
}