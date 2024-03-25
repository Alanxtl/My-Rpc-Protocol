package rpc.common.enums.extensionEnums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ServiceRegistryExtensionEnum {

    ZK("ZkServiceRegistry"),;

    public final String serviceName;

}
