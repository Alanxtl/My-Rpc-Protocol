package rpc.core.remoting.transport.netty.utils;

import rpc.core.remoting.dtos.RpcResponse;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UnprocessedRequests {

    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    private UnprocessedRequests() {}

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> resultFuture) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, resultFuture);
    }

    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        if (Optional.ofNullable(future).isPresent()) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException("Get a null future");
        }
    }
}
