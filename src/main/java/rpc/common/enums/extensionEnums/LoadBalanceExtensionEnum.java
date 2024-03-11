package rpc.common.enums.extensionEnums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum LoadBalanceExtensionEnum {

    CONSISTENT_HASH_LOAD_BALANCE("consistentHashLoadBalance"),
    RANDOM_LOAD_BALANCE("randomLoadBalance");

    public final String serviceName;

}
