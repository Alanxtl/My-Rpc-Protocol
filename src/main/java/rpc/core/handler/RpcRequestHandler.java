package rpc.core.handler;

import lombok.extern.slf4j.Slf4j;
import rpc.common.exception.RpcException;
import rpc.common.utils.SingletonFactory;
import rpc.core.provider.ServiceProvider;
import rpc.core.provider.zk.ZkServiceProvider;
import rpc.core.remoting.dtos.RpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        this.serviceProvider = SingletonFactory.getSingleton(ZkServiceProvider.class);
    }

    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());

        Object result;
        try {
            Method method = rpcRequest.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("Service [{}] successfully invoke method [{}]", rpcRequest.getRpcServiceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RpcException("Error occurred when Service [" + rpcRequest.getRpcServiceName() +
                    "] trying to invoke method [" + rpcRequest.getMethodName() + "]", e);
        }
        return result;
    }
}
