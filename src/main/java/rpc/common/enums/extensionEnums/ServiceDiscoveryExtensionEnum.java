package rpc.common.enums.extensionEnums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ServiceDiscoveryExtensionEnum {

    ZK("zk");

    public final String serviceName;

}
