package rpc.core.registry.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import rpc.common.configs.ZkConfig;
import rpc.common.enums.RpcExceptionEnum;
import rpc.common.exceptions.RpcException;
import rpc.core.extension.ExtensionLoader;
import rpc.core.registry.loadbalance.LoadBalance;
import rpc.core.registry.ServiceDiscovery;
import rpc.common.utils.CuratorUtil;
import rpc.core.remoting.dtos.RpcRequest;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(ZkConfig.loadBalance);
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        List<String> serviceUrlList = CuratorUtil.getChildrenNodes(zkClient, rpcServiceName);

        if (!Optional.ofNullable(serviceUrlList).isPresent() || serviceUrlList.isEmpty()) {
            throw new RpcException(RpcExceptionEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }

        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("Successfully found the service address: [{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);

        return new InetSocketAddress(host, port);
    }
}
