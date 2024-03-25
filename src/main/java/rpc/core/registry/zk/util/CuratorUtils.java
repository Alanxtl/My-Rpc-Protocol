package rpc.core.registry.zk.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import rpc.common.configs.RpcConfig;
import rpc.common.configs.ZkConfig;
import rpc.common.utils.PropertiesFileUtil;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CuratorUtils {

    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;

    private CuratorUtils() {
    }

    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            if (REGISTERED_PATH_SET.contains(path) || Optional.ofNullable(zkClient.checkExists().forPath(path)).isPresent()) {
                log.info("The persistent node with path [{}] already exists", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("Successfully created persistent node with path [{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("Failed to create persistent node with path [{}]", path);
        }
    }

    public static List<String> getChildrenNodes(CuratorFramework zkClient, String path) {
        if (SERVICE_ADDRESS_MAP.containsKey(path)) {
            return SERVICE_ADDRESS_MAP.get(path);
        }
        List<String> result = null;
        String servicePath = ZkConfig.ZK_REGISTER_ROOT_PATH + "/" + path;

        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(path, result);
            registerWatcher(path, zkClient);
        } catch (Exception e) {
            log.error("Failed to get children nodes for node [{}]", path);
        }

        return result;
    }

    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress) {
        REGISTERED_PATH_SET.parallelStream().forEach(p -> {
            try {
                if (p.endsWith(inetSocketAddress.toString())) {
                    zkClient.delete().forPath(p);
                }
            } catch (Exception e) {
                log.error("Clear registry for path [{}] failed", p);
            }
        });
        log.info("All registered services on the server are cleared: [{}]", REGISTERED_PATH_SET.toString());
    }

    public static CuratorFramework getZkClient() {
        Properties rpcConfigPath = PropertiesFileUtil.readPropertiesFile(RpcConfig.RPC_CONFIG_PATH);
        Properties zkAddress =  PropertiesFileUtil.readPropertiesFile(RpcConfig.ZK_ADDRESS);

        String zooKeeperAddress = "";
        if (Optional.ofNullable(rpcConfigPath).isPresent() && Optional.ofNullable(zkAddress).isPresent()) {
            zooKeeperAddress = zkAddress.toString();
        } else {
            zooKeeperAddress = ZkConfig.DEFAULT_ZK_ADDRESS;
        }

        log.info("Connecting to zookeeper server: [{}]", zooKeeperAddress);

        if (Optional.ofNullable(zkClient).isPresent() && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(ZkConfig.BASE_SLEEP_TIME, ZkConfig.MAX_RETRIES);

        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zooKeeperAddress)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();

        try {
            log.info("ZkClient is connecting");
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out connecting to zookeeper");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return zkClient;
    }


    private static void registerWatcher(String path, CuratorFramework zkClient) {
        String servicePath = ZkConfig.ZK_REGISTER_ROOT_PATH + "/" + path;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(path, serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            log.error("Cannot start register watcher", e);
        }
    }


}
