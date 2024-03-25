package rpc.common.utils;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public class PropertiesFileUtil {
    private PropertiesFileUtil() {
    }

    public static Properties readPropertiesFile(String filename) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String rpcConfigPath = "";
        if (Optional.ofNullable(url).isPresent()) {
            rpcConfigPath = url.getPath() + filename;
//            System.out.println(rpcConfigPath);
        }

        StringBuilder sb = new StringBuilder(rpcConfigPath);
        for( int i = 0; i < 3; i++ ) { //用以删除windows目录中的":"符号 如"/C:/Users/..." 中的冒号
            if( sb.charAt(i) == ':' ) {
                sb.deleteCharAt(i);
            }
        }

        rpcConfigPath = sb.toString();

        Properties properties = null;
        try (InputStreamReader inputStreamReader =
                     new InputStreamReader(Files.newInputStream(Paths.get(rpcConfigPath)),
                             StandardCharsets.UTF_8)) {
            properties = new Properties();
            properties.load(inputStreamReader);
        } catch (IOException e) {
            log.warn("Properties file [{}] not found", filename);
            return null;
        }
        return properties;
    }
}
