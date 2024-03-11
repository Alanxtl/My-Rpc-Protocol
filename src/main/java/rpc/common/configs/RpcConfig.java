package rpc.common.configs;

import lombok.Data;

@Data
public class RpcConfig {
    public static int RPC_SERVER_PORT = 9998;
    public static final String RPC_CONFIG_PATH = "rpc.properties";
    public static final String ZK_ADDRESS = "rpc.zookeeper.address";

}
