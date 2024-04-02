package rpc.core.remoting.transport.netty.utils;

import io.netty.channel.Channel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChannelProvider {
    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    private ChannelProvider() {}

    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();

        if ( channelMap.containsKey(key) ) {
            Channel channel = channelMap.get(key);

            if ( channel != null && channel.isActive() ) {
                return channel;
            } else {
                channelMap.remove(key);
            }
        }
        return null;
    }

    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        channelMap.put(key, channel);
    }

    public Channel remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        Channel ret = channelMap.remove(key);
        log.info("Channel map size: [{}]", channelMap.size());
        return ret;
    }
}
