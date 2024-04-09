package rpc.core.remoting.transport;

import rpc.core.extension.SPI;
import rpc.core.remoting.dtos.RpcRequest;

@SPI(messageType = "RpcRequestTransport")
public interface RpcRequestTransport {

    Object sendRpcRequest(RpcRequest rpcRequest);
}
