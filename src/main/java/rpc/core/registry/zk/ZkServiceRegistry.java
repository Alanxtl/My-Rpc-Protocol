package rpc.core.registry.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import rpc.common.configs.ZkConfig;
import rpc.core.registry.ServiceRegistry;
import rpc.common.utils.CuratorUtil;

import java.net.InetSocketAddress;

@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = ZkConfig.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        CuratorUtil.createPersistentNode(zkClient, servicePath);
    }

}
