package shared.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigLoader {
    private static final String CONFIG_PATH = "shared/config.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_PATH)) {
            if (inputStream == null) {
                throw new IllegalStateException("Config file not found: " + CONFIG_PATH);
            }
            PROPERTIES.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load config file: " + CONFIG_PATH, exception);
        }
    }

    private ConfigLoader() {
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static String getBaseUrl() {
        return get("base.url");
    }

    public static long getTimeoutSeconds() {
        return Long.parseLong(PROPERTIES.getProperty("default.timeout.seconds", "10"));
    }
}
