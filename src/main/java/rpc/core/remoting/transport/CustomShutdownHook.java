package rpc.core.remoting.transport;

import lombok.extern.slf4j.Slf4j;
import rpc.common.configs.RpcConfig;
import rpc.common.utils.ThreadPoolFactoryUtil;
import rpc.core.registry.zk.util.CuratorUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@Slf4j
public class CustomShutdownHook {
    // 单例通过SingletonFactory获取
    public CustomShutdownHook() {
    }

    public void clearAll() {
        log.info("Clearing all shutdown hook.");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), RpcConfig.RPC_SERVER_PORT);
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException ignored) {

            }
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }

}
