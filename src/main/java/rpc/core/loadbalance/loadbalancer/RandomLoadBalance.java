package rpc.core.loadbalance.loadbalancer;

import rpc.core.loadbalance.LoadBalance;
import rpc.core.remoting.dtos.RpcRequest;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {
    @Override
    public String doSelect(List<String> serviceUrls, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceUrls.get(random.nextInt(serviceUrls.size()));
    }
}
