package shared.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

public abstract class BaseTest {
    protected WebDriver driver;

    @BeforeEach
    protected void setUp() {
        driver = DriverFactory.createChromeDriver();
    }

    @AfterEach
    protected void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void openBaseUrl() {
        driver.get(ConfigLoader.getBaseUrl());
    }
}
