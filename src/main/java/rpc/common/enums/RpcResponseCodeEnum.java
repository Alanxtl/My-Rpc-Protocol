package rpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
public enum RpcResponseCodeEnum {

    SUCCESS((short) 200, "Remote procedure call success"),
    FAIL((short) 500, "Remote procedure call fail");

    private final short code;
    private final String message;

}
