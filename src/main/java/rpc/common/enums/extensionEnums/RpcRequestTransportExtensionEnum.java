package rpc.common.enums.extensionEnums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RpcRequestTransportExtensionEnum {

    SOCKET("socket"),
    NETTY("netty");

    public final String serviceName;
}
