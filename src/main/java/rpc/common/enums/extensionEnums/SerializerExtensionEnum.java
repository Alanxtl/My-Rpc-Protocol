package rpc.common.enums.extensionEnums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializerExtensionEnum {

    KYRO((byte) 1, "kyro"),
    PROTOSTUFF((byte) 2, "protostuff"),
    HESSIAN((byte) 3, "hessian"),
    JACKSON((byte) 4, "jackson");

    public final Byte code;
    public final String serviceName;

    public static String getEnum(byte code) {
        for (SerializerExtensionEnum c : SerializerExtensionEnum.values()) {
            if (c.getCode() == code) {
                return c.serviceName;
            }
        }
        return null;
    }
}
