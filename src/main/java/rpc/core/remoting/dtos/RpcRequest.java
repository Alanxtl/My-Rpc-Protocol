package rpc.core.remoting.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RpcRequest implements Serializable {

    private String requestId; // 调用编号
    private String interfaceName; // 类名
    private String methodName; // 方法名
    private Class<?>[] parameterTypes; // 请求参数的数据类型
    private Object[] parameters; // 请求的参数
    private String version; // 服务版本
    private String group; // 服务分组

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }

    public static void main(String[] args) {
        RpcRequest rpcRequest = RpcRequest.builder().requestId("123").interfaceName("rpc").methodName("rpc").parameterTypes(new Class<?>[0]).parameters(new Object[0]).version("1.0").group("rpc").build();
        System.out.println(rpcRequest.toString());
    }
}
