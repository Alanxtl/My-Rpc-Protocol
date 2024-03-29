package rpc.core.loadbalance.loadbalancer;

import rpc.core.loadbalance.LoadBalance;
import rpc.core.remoting.dtos.RpcRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConsistentHashLoadBalance implements LoadBalance {
    private final ConcurrentHashMap<String, ConstantHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    public String doSelect(List<String> serviceUrls, RpcRequest rpcRequest) {
        int identityHashCode = System.identityHashCode(serviceUrls);
        String rpcServiceName = rpcRequest.getRpcServiceName();
        ConstantHashSelector selector = selectors.get(rpcServiceName);

        if ( !Optional.ofNullable(selector).isPresent() || selector.identityHashCode != identityHashCode) {
            selectors.put(rpcServiceName, new ConstantHashSelector(serviceUrls, 160, identityHashCode));
            selector = selectors.get(rpcServiceName);
        }

        return selector.select(rpcServiceName + Arrays.stream(rpcRequest.getParameters()));
    }


    static class ConstantHashSelector {
        private final TreeMap<Long, String> virtualInvokers = new TreeMap<>();

        private final int identityHashCode;

        ConstantHashSelector(List<String> invokers, int replicaNumber, int identityHashCode) {
            this.identityHashCode = identityHashCode;

            for ( String in : invokers ) {
                for ( int i = 0; i < replicaNumber / 4; ++i ) {
                    byte[] digest = md5(in + 1);
                    for ( int h = 0; h < 4; ++h) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, in);
                    }
                }
            }
        }


        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        static long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        public String select(String rpcServiceKey) {
            byte[] digest = md5(rpcServiceKey);
            return selectForKey(hash(digest, 0));
        }

        public String selectForKey(long hashCode) {
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();

            if ( !Optional.ofNullable(entry).isPresent() ) {
                entry = virtualInvokers.firstEntry();
            }

            return entry.getValue();
        }
    }

}
