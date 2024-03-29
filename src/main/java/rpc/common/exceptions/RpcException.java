package rpc.common.exceptions;

import rpc.common.enums.RpcExceptionEnum;

public class RpcException extends RuntimeException{

    public RpcException(RpcExceptionEnum message, String cause, Throwable e) {
        super(message.getMessage() + " : " + cause, e);
    }


    public RpcException(RpcExceptionEnum message, String cause) {
        super(message.getMessage() + ":" + cause);
    }

    public RpcException(RpcExceptionEnum message, Throwable cause) {
        super(message.getMessage(), cause);
    }
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
    public RpcException(String message) {
        super(message);
    }
    public RpcException(Throwable cause) {
        super(cause);
    }
}
