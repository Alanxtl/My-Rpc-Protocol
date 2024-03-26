package rpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    GZIP((byte) 1, "gzip");

    private final byte code;
    private final String name;

    public static String getEnum(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
