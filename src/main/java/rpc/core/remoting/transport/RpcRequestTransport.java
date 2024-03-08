package rpc.core.remoting.transport;


import rpc.common.extension.SPI;
import rpc.core.remoting.dtos.RpcRequest;

@SPI
public interface RpcRequestTransport {
    int PORT = 9998;

    Object sendRpcRequest(RpcRequest rpcRequest);
}
