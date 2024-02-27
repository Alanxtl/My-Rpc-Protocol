package lxt.example.protocol;

import lombok.Data;

@Data
public class RpcResponse {
    private String requestId; // 调用编号
    private Throwable throwable; // 异常信息
    private Object result; // 返回结果
}
