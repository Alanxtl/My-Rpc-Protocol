package rpc.core.remoting.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NettyRpcMessage {
    /**
     * 消息类型，见NettyMessageTypeEnum
     */
    private byte messageType;
    /**
     * 序列化类型
     */
    private byte serializeType;
    /**
     * 压缩类型
     */
    private byte compressType;
    /**
     * 请求ID
     */
    private int requestId;
    /**
     * 数据部分 是RpcRequest/RpcResponse或Ping/Pong
     */
    private Object data;
}
