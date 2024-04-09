package rpc.core.registry;

import rpc.core.extension.SPI;

import java.net.InetSocketAddress;

@SPI(messageType = "ServiceRegistry")
public interface ServiceRegistry {

    /**
    * 注册服务到注册中心
    * @param rpcServiceName 完整的请求类（关键请求信息为 name+group+version）
    * @param inetSocketAddress 远程服务地址
    */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
