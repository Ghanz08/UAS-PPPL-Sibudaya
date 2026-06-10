package sibudaya.e2e.pages;

import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import shared.core.ConfigLoader;

public class AuthPage extends BaseE2ePage {
    private static final String USER_IDENTIFIER = "sibudaya.e2e.user.identifier";
    private static final String USER_PASSWORD = "sibudaya.e2e.user.password";
    private static final String SUPERADMIN_IDENTIFIER = "sibudaya.e2e.superadmin.identifier";
    private static final String SUPERADMIN_PASSWORD = "sibudaya.e2e.superadmin.password";

    public AuthPage(WebDriver driver) {
        super(driver);
    }

    public void assertCredentialsConfigured() {
        requiredConfig(USER_IDENTIFIER);
        requiredConfig(USER_PASSWORD);
        requiredConfig(SUPERADMIN_IDENTIFIER);
        requiredConfig(SUPERADMIN_PASSWORD);
    }

    public void loginAsUser() {
        login(requiredConfig(USER_IDENTIFIER), requiredConfig(USER_PASSWORD));
        openPath("/dashboard");
        waitForUrlContains("/dashboard");
    }

    public void loginAsSuperadmin() {
        login(requiredConfig(SUPERADMIN_IDENTIFIER), requiredConfig(SUPERADMIN_PASSWORD));
        openPath("/dashboard/admin");
        waitForUrlContains("/dashboard/admin");
    }

    private void login(String identifier, String password) {
        clearSession();
        openPath("/login");
        WebElement identifierInput = waitForPage().until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='NIK Lembaga atau Email Admin'], input[name='identifier'], input[type='text'], input[type='email']")
        ));
        identifierInput.clear();
        identifierInput.sendKeys(identifier);

        WebElement passwordInput = driver.findElement(By.cssSelector("input[type='password']"));
        passwordInput.clear();
        passwordInput.sendKeys(password);
        driver.findElement(By.xpath("//button[contains(normalize-space(.), 'Masuk')]")).click();

        waitForPage().until(webDriver -> Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                "return Boolean(window.localStorage.getItem('access_token') && window.localStorage.getItem('refresh_token'));"
        )));
        copyTokensToCookies();
    }

    private void copyTokensToCookies() {
        String accessToken = (String) ((JavascriptExecutor) driver).executeScript("return window.localStorage.getItem('access_token');");
        String refreshToken = (String) ((JavascriptExecutor) driver).executeScript("return window.localStorage.getItem('refresh_token');");
        driver.get(ConfigLoader.getBaseUrl().replaceAll("/+$", "/"));
        driver.manage().addCookie(new Cookie.Builder("access_token", accessToken).path("/").isHttpOnly(true).isSecure(true).build());
        driver.manage().addCookie(new Cookie.Builder("refresh_token", refreshToken).path("/").isHttpOnly(true).isSecure(true).build());
        ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.setItem('access_token', arguments[0]); window.localStorage.setItem('refresh_token', arguments[1]);",
                accessToken,
                refreshToken
        );
    }

    private void clearSession() {
        driver.manage().deleteAllCookies();
        openPath("/");
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
    }

    private String requiredConfig(String key) {
        String value = ConfigLoader.getOptional(key);
        Assumptions.assumeTrue(value != null, "Missing required config: " + key);
        return value;
    }
}
