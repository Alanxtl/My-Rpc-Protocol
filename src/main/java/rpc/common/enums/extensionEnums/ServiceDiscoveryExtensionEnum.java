package rpc.common.enums.extensionEnums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ServiceDiscoveryExtensionEnum {

    ZK("ZkServiceDiscovery");

    public final String serviceName;

}
