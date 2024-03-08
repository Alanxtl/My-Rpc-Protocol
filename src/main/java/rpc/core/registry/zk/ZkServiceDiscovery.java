package rpc.core.registry.zk;

import lombok.extern.slf4j.Slf4j;
import rpc.core.registry.ServiceDiscovery;
import rpc.core.remoting.dtos.RpcRequest;

import java.net.InetSocketAddress;

@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    // TODO
    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        return null;
    }
}
