package rpc.core.remoting.transport.socket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import rpc.common.extension.ExtensionLoader;
import rpc.core.registry.ServiceDiscovery;
import rpc.core.remoting.dtos.RpcRequest;
import rpc.core.remoting.transport.RpcRequestTransport;
import rpc.core.serialize.Serializer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@AllArgsConstructor
@Builder
@Slf4j
public class SocketRpcClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            // 使用outputStream向server传递object
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcRequest);
            // 使用inputStream读取RpcResponse
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Unable to send the rpc request.", e);
        }
    }

    public static void main(String[] args) {
//        SocketRpcClient socketRpcClient = new SocketRpcClient();
//        RpcRequest rpcRequest = new RpcRequest();
//        socketRpcClient.sendRpcRequest(rpcRequest);

        Serializer s = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension("protostuff");
        System.out.println(s);

    }
}
