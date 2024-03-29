package rpc.common.configs;

import rpc.common.enums.extensionEnums.CompressExtensionEnum;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class NettyConfig {

    private static final byte DEFAULT_COMPRESS_TYPE = CompressExtensionEnum.GZIP.getCode();
    private static final byte DEFAULT_SERIALIZER_TYPE = RpcConfig.serializerType;
    public static final byte[] MAGIC_NUMBER = {(byte) 'A', (byte) 'l', (byte) 'a', (byte) 'n'};
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final byte VERSION = 1;
    public static final byte TOTAL_LENGTH = 16;
    public static final int HEAD_LENGTH = 16;
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
    public static byte compressType = DEFAULT_COMPRESS_TYPE;
    public static byte serializerType = DEFAULT_SERIALIZER_TYPE;

    static {
        try {
            ResourceBundle rb = ResourceBundle.getBundle("nettyConfig");
            for (String key : rb.keySet()) {
                String value = rb.getString(key);

                switch (key) {
                    case "compressType":
                        compressType = Byte.parseByte(value);
                        break;
                    case "serializerType":
                        serializerType = Byte.parseByte(value);
                        break;
                    default:
                        throw new RuntimeException("Unknown config item [" + key + "] in [" + rb.getBaseBundleName() + "].");
                }
            }
        } catch (MissingResourceException ignored) {}
    }

}
