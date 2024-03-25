package rpc.core.proxy;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rpc.common.enums.RpcExceptionEnum;
import rpc.common.exception.RpcException;
import rpc.core.provider.TargetRpcService;
import rpc.core.remoting.dtos.RpcRequest;
import rpc.core.remoting.dtos.RpcResponse;
import rpc.core.remoting.transport.RpcRequestTransport;
import rpc.core.remoting.transport.netty.NettyRpcClient;
import rpc.core.remoting.transport.socket.SocketRpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private final RpcRequestTransport rpcRequestTransport;
    private final TargetRpcService targetRpcService;

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, TargetRpcService targetRpcService) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.targetRpcService = targetRpcService;
    }

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.targetRpcService = new TargetRpcService();
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("Invoking method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder()
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes())
                .interfaceName(method.getDeclaringClass().getName())
                .requestId(UUID.randomUUID().toString())
                .group(targetRpcService.getGroup())
                .version(targetRpcService.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = null;

        if (rpcRequestTransport instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) rpcRequestTransport.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        } else if (rpcRequestTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<Object>) rpcRequestTransport.sendRpcRequest(rpcRequest);
        }
        this.check(rpcResponse, rpcRequest);

        return rpcResponse.getResult();

    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcExceptionEnum.SERVICE_INVOCATION_FAILURE, "interfaceName : " + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcExceptionEnum.REQUEST_NOT_MATCH_RESPONSE, "interfaceName : " + rpcRequest.getInterfaceName());
        }
//
//        if (rpcResponse.getResponse() == null || !rpcResponse.getResponse().equals(RpcResponseCodeEnum.SUCCESS)) {
//            throw new RpcException(RpcExceptionEnum.SERVICE_INVOCATION_FAILURE, "interfaceName : " + rpcRequest.getInterfaceName());
//        }
    }
}