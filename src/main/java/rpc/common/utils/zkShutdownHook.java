package rpc.common.utils;

import lombok.extern.slf4j.Slf4j;
import rpc.common.configs.RpcConfig;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * 若需使用此工具，请使用SingletonFactory获取单例。
 */
@Slf4j
public class zkShutdownHook {
    private zkShutdownHook() {}
    public void clearAll() {
        log.info("Clearing existing zookeeper registry.");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), RpcConfig.rpcServerPort);
                CuratorUtil.clearRegistry(CuratorUtil.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException ignored) {

            }
            ThreadPoolUtil.shutDownAllThreadPool();
        }));
    }

}
