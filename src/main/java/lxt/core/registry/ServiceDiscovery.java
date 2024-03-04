package lxt.core.registry;

import lxt.common.extension.SPI;
import lxt.core.remoting.dtos.RpcRequest;

import java.net.InetSocketAddress;

@SPI
public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
