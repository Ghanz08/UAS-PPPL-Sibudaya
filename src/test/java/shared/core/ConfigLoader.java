package shared.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

public final class ConfigLoader {
    private static final String CONFIG_PATH = "shared/config.properties";
    private static final Properties PROPERTIES = new Properties();
    private static final Map<String, String> DEFAULTS = Map.of(
            "base.url", "https://www.sibudaya.cloud/sibudaya",
            "default.timeout.seconds", "10"
    );

    static {
        loadClasspathConfigIfPresent();
        loadExternalConfigIfPresent();
    }

    private ConfigLoader() {
    }

    private static void loadExternalConfigIfPresent() {
        String configFile = System.getProperty("config.file");
        if (configFile == null || configFile.isBlank()) {
            configFile = System.getenv("CONFIG_FILE");
        }

        if (configFile == null || configFile.isBlank()) {
            return;
        }

        Path path = Path.of(configFile);
        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException("Config file not found: " + path.toAbsolutePath());
        }

        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            PROPERTIES.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load config file: " + path.toAbsolutePath(), exception);
        }
    }

    private static void loadClasspathConfigIfPresent() {
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_PATH)) {
            if (inputStream != null) {
                PROPERTIES.load(inputStream);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load config file: " + CONFIG_PATH, exception);
        }
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

        String propertyValue = PROPERTIES.getProperty(key);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        return DEFAULTS.get(key);
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
        return getRequired("base.url");
    }

    public static long getTimeoutSeconds() {
        return Long.parseLong(getRequired("default.timeout.seconds"));
    }

    private static String getRequired(String key) {
        String value = getOptional(key);
        if (value == null) {
            throw new IllegalStateException("Missing required config: " + key
                    + ". Set -D" + key + "=... or env " + key.toUpperCase().replace('.', '_') + ".");
        }

        return value;
    }
}
