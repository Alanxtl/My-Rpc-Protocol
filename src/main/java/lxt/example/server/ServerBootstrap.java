//package lxt.example.server;
//
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.handler.logging.LogLevel;
//import io.netty.handler.logging.LoggingHandler;
//import org.springframework.context.annotation.Bean;
//
//import java.util.Map;
//import java.util.Set;
//
//
//public class Server {
//
//    @Bean
//    public ServerBootstrap serverBootstrap() {
//        ServerBootstrap serverBootstrap = new ServerBootstrap();
//        serverBootstrap.group(bossGroup(), workerGroup())
//                .channel(NioServerSocketChannel.class)
//                .handler(new LoggingHandler(LogLevel.DEBUG))
//                .childHandler(serverInitializer);
//        return new ServerBootstrap();
//
//       Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
//       Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
//
//       for (@SuppressWarnings("rawtypes") ChannelOption option: keySet) {
//           serverBootstrap.option(option, tcpChannelOptions.get(option));
//       }
//
//       return serverBootstrap;
//
//    }
//}
