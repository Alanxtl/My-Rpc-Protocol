package lxt.core.remoting.dtos;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

import lxt.common.enums.RpcResponseCodeEnum;

@Builder
@Data
public class RpcResponse implements Serializable {
    private String requestId; // 调用编号
    private RpcResponseCodeEnum response; // 调用结果
    private Throwable throwable; // 异常信息
    private Object result; // 返回结果

    public static RpcResponse success(Object result, String requestId) {
        return RpcResponse.builder()
                .result(result)
                .requestId(requestId)
                .response(RpcResponseCodeEnum.SUCCESS).build();
    }

    public static RpcResponse fail(Throwable throwable, String requestId) {
        return RpcResponse.builder()
                .throwable(throwable)
                .requestId(requestId)
                .response(RpcResponseCodeEnum.FAIL).build();
    }
}
