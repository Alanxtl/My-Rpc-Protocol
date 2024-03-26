package socketTest;

import rpc.core.provider.TargetRpcService;
import rpc.core.proxy.RpcClientProxy;
import rpc.core.remoting.transport.RpcRequestTransport;
import rpc.core.remoting.transport.socket.SocketRpcClient;
import targetClasses.*;

public class ClientMain {



    public static void main(String[] args) {

        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();
        TargetRpcService targetRpcService = TargetRpcService.builder()
                .group("test1")
                .version("version1")
                .build();

        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, targetRpcService);
        ClassTransferTest classTransferTest = rpcClientProxy.getProxy(ClassTransferTest.class);

        ClassOut out = classTransferTest.testTransferClass(new ClassIn());
        System.out.println(out);

    }
}
