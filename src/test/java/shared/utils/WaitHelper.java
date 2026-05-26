package shared.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import shared.core.ConfigLoader;

import java.time.Duration;

public final class WaitHelper {
    private WaitHelper() {
    }

    public static WebDriverWait defaultWait(WebDriver webDriver) {
        return new WebDriverWait(webDriver, Duration.ofSeconds(ConfigLoader.getTimeoutSeconds()));
    }
}
