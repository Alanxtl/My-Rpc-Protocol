package lxt.core.remoting.transport.socket;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import lxt.common.extension.ExtensionLoader;
import lxt.core.registry.ServiceDiscovery;
import lxt.core.remoting.dtos.RpcRequest;
import lxt.core.remoting.transport.RpcRequestTransport;

@Builder
@Slf4j
public class SocketRpcClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        return null;
    }
}
