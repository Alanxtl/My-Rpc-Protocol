package rpc.core.remoting.transport;


import rpc.common.extension.SPI;
import rpc.core.remoting.dtos.RpcRequest;

@SPI
public interface RpcRequestTransport {

    Object sendRpcRequest(RpcRequest rpcRequest);
}
