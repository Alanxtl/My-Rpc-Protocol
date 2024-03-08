package rpc.core.config;

import lombok.extern.slf4j.Slf4j;
import rpc.core.registry.zk.util.CuratorUtils;
import rpc.common.utils.concurrent.ThreadPoolFactoryUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static rpc.core.remoting.transport.RpcRequestTransport.PORT;

@Slf4j
public class CustomShutdownHook {

    // 单例通过SingletonFactory获取
    private CustomShutdownHook() {}


    // TODO
    public void clearAll() {
        log.info("Clearing all shutdown hook.");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PORT);
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException ignored) {

            }
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }

}
