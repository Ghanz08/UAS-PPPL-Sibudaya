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
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String envValue = System.getenv(key.toUpperCase().replace('.', '_'));
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return PROPERTIES.getProperty(key);
    }

    public static String getOptional(String key) {
        String value = get(key);
        if (value == null || value.isBlank() || value.startsWith("<")) {
            return null;
        }

        return value.trim();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = getOptional(key);
        if (value == null) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value);
    }

    public static String getBaseUrl() {
        return get("base.url");
    }

    public static long getTimeoutSeconds() {
        return Long.parseLong(PROPERTIES.getProperty("default.timeout.seconds", "10"));
    }
}
