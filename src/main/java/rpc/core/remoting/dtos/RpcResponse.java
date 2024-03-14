package rpc.core.remoting.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import rpc.common.enums.RpcResponseCodeEnum;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {
    private String requestId; // 调用编号
    private RpcResponseCodeEnum response; // 调用结果
    //    private Throwable throwable; // 异常信息
    private T result; // 返回结果 有可能是 Throwable

    public static <T> RpcResponse<T> success(T result, String requestId) {
        RpcResponse<T> res = new RpcResponse<>();
        res.setResult(result);
        res.setRequestId(requestId);
        res.setResponse(RpcResponseCodeEnum.SUCCESS);
        return res;
    }

    public static RpcResponse<Throwable> fail(Throwable throwable, String requestId) {
        RpcResponse<Throwable> res = new RpcResponse<>();
        res.setResult(throwable);
        res.setRequestId(requestId);
        res.setResponse(RpcResponseCodeEnum.SUCCESS);

        return res;
    }
}
