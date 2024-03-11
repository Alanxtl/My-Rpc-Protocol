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
        }
        Properties properties = null;
        try (InputStreamReader inputStreamReader =
                     new InputStreamReader(Files.newInputStream(Paths.get(rpcConfigPath)),
                             StandardCharsets.UTF_8)) {
            properties = new Properties();
            properties.load(inputStreamReader);
        } catch (IOException e) {
            log.error("Cannot read properties file [{}]", filename);
        }
        return properties;
    }
}
