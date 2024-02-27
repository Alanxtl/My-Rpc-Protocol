package lxt.example.protocol;

@Data
public class RpcRequest {
    private String requestId; // 调用编号
    private String className; // 类名
    private String methodName; // 方法名
    private Class<?>[] parameterTypes; // 请求参数的数据类型
    private Object[] parameters; // 请求的参数
}
