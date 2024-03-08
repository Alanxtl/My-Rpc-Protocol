package rpc.core.registry;

import rpc.common.extension.SPI;

import java.net.InetSocketAddress;

@SPI
public interface ServiceRegistry {
    // 服务注册类接口
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
