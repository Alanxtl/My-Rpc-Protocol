package lxt.core.registry;

import lxt.core.remoting.dtos.RpcRequest;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
