package rpc.test.socketTest;

import lombok.extern.slf4j.Slf4j;
import rpc.core.provider.TargetRpcService;
import rpc.core.remoting.transport.socket.SocketRpcServer;
import rpc.test.targetClasses.HelloService2Impl;
import rpc.test.targetClasses.ClassTransferTestImpl;


@Slf4j
public class ServerMain {


    public static void main(String[] args) {
        TargetRpcService targetRpcService = TargetRpcService.builder()
                .group("test1")
                .version("version1")
                .service(new ClassTransferTestImpl())
                .build();
        TargetRpcService targetRpcService2 = TargetRpcService.builder()
                .group("test2")
                .version("version2")
                .service(new HelloService2Impl())
                .build();
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        socketRpcServer.registerService(targetRpcService);
        socketRpcServer.registerService(targetRpcService2);
        socketRpcServer.start();

    }
}
