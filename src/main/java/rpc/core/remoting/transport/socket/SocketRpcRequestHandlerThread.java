package rpc.core.remoting.transport.socket;

import lombok.extern.slf4j.Slf4j;
import rpc.common.enums.extensionEnums.SerializerExtensionEnum;
import rpc.common.extension.ExtensionLoader;
import rpc.common.utils.SingletonFactory;
import rpc.core.handler.RpcRequestHandler;
import rpc.core.remoting.dtos.RpcRequest;
import rpc.core.remoting.dtos.RpcResponse;
import rpc.core.serialize.Serializer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class SocketRpcRequestHandlerThread implements Runnable {
    private final Socket socket;
    private final RpcRequestHandler rpcRequestHandler;
    private final Serializer serializer;

    public SocketRpcRequestHandlerThread(Socket socket) {
        this.socket = socket;
        this.rpcRequestHandler = SingletonFactory.getSingleton(RpcRequestHandler.class);
        this.serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(SerializerExtensionEnum.PROTOSTUFF.serviceName);
    }

    @Override
    public void run() {
        log.info("Server thread [{}] is handling your message", Thread.currentThread().getName());
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {

            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object result = rpcRequestHandler.handle(rpcRequest);

            if (result instanceof Throwable) {
                objectOutputStream.writeObject(RpcResponse.fail((Throwable) result, rpcRequest.getRequestId()));
            } else {
                objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            }
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error occurred when thread [" + Thread.currentThread().getName() + "] handling message", e);
        }
    }
}
