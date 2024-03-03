package lxt.core.remoting.transport;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lxt.core.remoting.dtos.RpcRequest;


public interface RpcRequestTransport {
    Object sendRpcRequest(RpcRequest rpcRequest);
}
