package rpc.core.remoting.transport.socket;


import lombok.extern.slf4j.Slf4j;
import rpc.common.utils.SingletonFactory;
import rpc.common.utils.ThreadPoolUtil;
import rpc.core.handler.RpcRequestHandlerThread;
import rpc.common.utils.zkShutdownHook;
import rpc.core.provider.TargetRpcService;
import rpc.core.provider.ServiceProvider;
import rpc.core.provider.zk.ZkServiceProvider;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static rpc.common.configs.RpcConfig.rpcServerPort;

@Slf4j
public class SocketRpcServer {
//    private final ExecutorService threadPoll;
    private final String threadPoolName;
    private final ServiceProvider serviceProvider;

    public SocketRpcServer() {
        this.threadPoolName = "socket-server-rpc-pool";
        ThreadPoolUtil.createCustomThreadPoolIfAbsent(this.threadPoolName);
        this.serviceProvider = SingletonFactory.getSingleton(ZkServiceProvider.class);
    }

    public SocketRpcServer(String threadPoolName) {
        this.threadPoolName = threadPoolName;
        ThreadPoolUtil.createCustomThreadPoolIfAbsent(this.threadPoolName);
        this.serviceProvider = SingletonFactory.getSingleton(ZkServiceProvider.class);
    }

    public void registerService(TargetRpcService targetRpcService) {
        serviceProvider.publishService(targetRpcService);
    }

    public void start() {
        try (ServerSocket server = new ServerSocket()) {
            String host = InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, rpcServerPort));
            SingletonFactory.getSingleton(zkShutdownHook.class).clearAll();
            Socket socket;
            while (Optional.ofNullable(socket = server.accept()).isPresent()) {
                log.info("Client [{}] connected", socket.getInetAddress());
                ThreadPoolUtil.execute(threadPoolName, new RpcRequestHandlerThread(socket));
            }
            ThreadPoolUtil.shutDown(threadPoolName);
        } catch (IOException e) {
            log.error("Caught IOException when starting rpc server:", e);
        }
    }

    public static void main(String[] args) {
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        socketRpcServer.start();
    }


}
