package rpc.core.provider;

public interface ServiceProvider {
    void addService(RpcServiceConfig rpcServiceConfig);
    Object getService(String rpcServiceName);
    void publishService(RpcServiceConfig rpcServiceConfig);

}