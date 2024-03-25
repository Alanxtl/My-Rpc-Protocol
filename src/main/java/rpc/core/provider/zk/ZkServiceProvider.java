package rpc.core.provider.zk;

import lombok.extern.slf4j.Slf4j;
import rpc.common.configs.ZkConfig;
import rpc.common.enums.RpcExceptionEnum;
import rpc.common.exception.RpcException;
import rpc.common.extension.ExtensionLoader;
import rpc.core.provider.TargetRpcService;
import rpc.core.provider.ServiceProvider;
import rpc.core.registry.ServiceRegistry;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static rpc.common.configs.RpcConfig.RPC_SERVER_PORT;

@Slf4j
public class ZkServiceProvider implements ServiceProvider {

    private final Map<String, Object> serviceMap;
    private final Set<String> registeredService;
    private final ServiceRegistry serviceRegistry;

    public ZkServiceProvider() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension(ZkConfig.ZK_SERVICE_PROVIDER);
    }

    @Override
    public void addService(TargetRpcService targetRpcService) {
        String serviceName = targetRpcService.getRpcServiceName();
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName, targetRpcService.getService());
        log.info("Add service: <{}, {}>", serviceName, targetRpcService.getService().getClass());
    }

    @Override
    public Object getService(String rpcServiceName) {
        if (!Optional.ofNullable(rpcServiceName).isPresent() || rpcServiceName.isEmpty()) {
            throw new RpcException("Empty or null rpc service name");
        }

        Object service = serviceMap.get(rpcServiceName);
        if (Optional.ofNullable(service).isPresent()) {
            return service;
        } else {
            throw new RpcException(RpcExceptionEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
    }

    @Override
    public void publishService(TargetRpcService targetRpcService) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(targetRpcService);
            serviceRegistry.registerService(targetRpcService.getRpcServiceName(), new InetSocketAddress(host, RPC_SERVER_PORT));
        } catch (UnknownHostException e) {
            log.error("Cannot get host address", e);
        }
    }
}
