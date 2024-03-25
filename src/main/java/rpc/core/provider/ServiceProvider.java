package rpc.core.provider;

public interface ServiceProvider {
    void addService(TargetRpcService targetRpcService);
    Object getService(String rpcServiceName);
    void publishService(TargetRpcService targetRpcService);

}