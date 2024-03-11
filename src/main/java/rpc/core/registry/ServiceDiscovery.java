package rpc.core.registry;

import rpc.common.extension.SPI;
import rpc.core.remoting.dtos.RpcRequest;

import java.net.InetSocketAddress;

@SPI
public interface ServiceDiscovery {

    /**
    * 根据 rpcRequest 获取远程服务地址
    * @param rpcRequest 完整的请求类（关键请求信息为 name+group+version）
    * @return 远程服务地址
    */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
