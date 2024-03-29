package rpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
public enum NettyMessageTypeEnum {
    REQUEST((byte) 1),
    RESPONSE((byte) 2),
    HEARTBEAT_REQUEST((byte) 3),
    HEARTBEAT_RESPONSE((byte) 4),;

    private final byte code;
}
