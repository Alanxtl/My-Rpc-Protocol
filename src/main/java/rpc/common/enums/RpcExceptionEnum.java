package rpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum RpcExceptionEnum {
    CLIENT_CONNECT_SERVER_FAILURE("Client failed to connect to the server "),
    SERVICE_INVOCATION_FAILURE("Service invoke failed "),
    SERVICE_CAN_NOT_BE_FOUND("The corresponding service cannot be found "),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("Registered service does not implement any interface "),
    REQUEST_NOT_MATCH_RESPONSE("The type of the request is not consistent with the type of the response "),;

    private final String message;

}
