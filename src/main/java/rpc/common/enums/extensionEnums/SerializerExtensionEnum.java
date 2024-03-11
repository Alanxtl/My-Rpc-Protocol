package rpc.common.enums.extensionEnums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SerializerExtensionEnum {

    KYRO("kyro"),
    PROTOSTUFF("protostuff"),
    HESSIAN("hessian"),
    jackson("jackson");

    public final String serviceName;
}
