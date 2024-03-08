package rpc.core.registry;

import rpc.common.extension.SPI;
import rpc.core.remoting.dtos.RpcRequest;

import java.net.InetSocketAddress;

@SPI
public interface ServiceDiscovery {
    // 服务发现类接口
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
