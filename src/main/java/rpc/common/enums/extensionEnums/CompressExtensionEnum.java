package rpc.common.enums.extensionEnums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompressExtensionEnum {

    GZIP((byte) 1, "gzip");

    private final byte code;
    private final String name;

    public static String getEnum(byte code) {
        for (CompressExtensionEnum c : CompressExtensionEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
