package rpc.core.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TargetRpcService {

    /**
     * 实现类的版本号
     */
    private String version = "#";
    /**
     * 使用group区分目标服务的不同实现类
     */
    private String group = "#";
    /**
     * 目标服务
     */
    private Object service;

    public String getRpcServiceName() {
        return this.getServiceName() + "/" + this.getGroup() + "/" + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

}
