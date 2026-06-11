package shared.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class WaitHelper {
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    private WaitHelper() {
    }

    public static WebDriverWait defaultWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
    }
}
