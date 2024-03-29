package rpc.core.registry.loadbalance;

import rpc.core.extension.SPI;
import rpc.core.remoting.dtos.RpcRequest;

import java.util.List;
import java.util.Optional;

@SPI
public interface LoadBalance {
    default String selectServiceAddress(List<String> serviceUrls, RpcRequest rpcRequest) {
        if (!Optional.ofNullable(serviceUrls).isPresent() || serviceUrls.isEmpty()) {
            return null;
        } else if (serviceUrls.size() == 1) {
            return serviceUrls.get(0);
        }
        return doSelect(serviceUrls, rpcRequest);
    }

    String doSelect(List<String> serviceUrls, RpcRequest rpcRequest);
}
