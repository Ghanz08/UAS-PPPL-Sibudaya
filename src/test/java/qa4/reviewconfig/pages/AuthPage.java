package qa4.reviewconfig.pages;

import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import shared.core.ConfigLoader;

public class AuthPage extends BasePage {
    private static final String ADMIN_IDENTIFIER = "qa4.admin.identifier";
    private static final String ADMIN_PASSWORD = "qa4.admin.password";
    private static final String LEMBAGA_IDENTIFIER = "qa4.lembaga.identifier";
    private static final String LEMBAGA_PASSWORD = "qa4.lembaga.password";

    public AuthPage(WebDriver driver) {
        super(driver);
    }

    public void loginAsAdmin() {
        login(requiredConfig(ADMIN_IDENTIFIER), requiredConfig(ADMIN_PASSWORD));
        openAuthenticatedPath("/dashboard/admin");
        waitForUrlContains("/dashboard/admin");
    }

    public void loginAsLembaga() {
        login(requiredConfig(LEMBAGA_IDENTIFIER), requiredConfig(LEMBAGA_PASSWORD));
        openAuthenticatedPath("/dashboard");
        waitForUrlContains("/dashboard");
    }

    public void login(String identifier, String password) {
        clearBrowserSession();
        openBaseUrlPath("/login");

        WebElement identifierInput = pageWait().until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='NIK Lembaga atau Email Admin']")
        ));
        identifierInput.clear();
        identifierInput.sendKeys(identifier);

        WebElement passwordInput = driver.findElement(By.cssSelector("input[type='password']"));
        passwordInput.clear();
        passwordInput.sendKeys(password);

        driver.findElement(By.xpath("//button[normalize-space()='Masuk']")).click();

        pageWait().until(webDriver -> ((JavascriptExecutor) webDriver).executeScript(
                "return Boolean(window.localStorage.getItem('access_token') && window.localStorage.getItem('refresh_token'));"
        ).equals(Boolean.TRUE));

        String accessToken = (String) ((JavascriptExecutor) driver).executeScript("return window.localStorage.getItem('access_token');");
        String refreshToken = (String) ((JavascriptExecutor) driver).executeScript("return window.localStorage.getItem('refresh_token');");

        driver.get(ConfigLoader.getBaseUrl().replaceAll("/+$", "/"));
        driver.manage().addCookie(new Cookie.Builder("access_token", accessToken).path("/").isHttpOnly(true).isSecure(true).build());
        driver.manage().addCookie(new Cookie.Builder("refresh_token", refreshToken).path("/").isHttpOnly(true).isSecure(true).build());
        ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.setItem('access_token', arguments[0]);" +
                        "window.localStorage.setItem('refresh_token', arguments[1]);",
                accessToken,
                refreshToken
        );
    }

    public void assertQa4AccountsConfigured() {
        requiredConfig(ADMIN_IDENTIFIER);
        requiredConfig(ADMIN_PASSWORD);
        requiredConfig(LEMBAGA_IDENTIFIER);
        requiredConfig(LEMBAGA_PASSWORD);
    }

    private void clearBrowserSession() {
        driver.manage().deleteAllCookies();
        openBaseUrlPath("/");
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
    }

    private String requiredConfig(String key) {
        String value = ConfigLoader.getOptional(key);
        Assumptions.assumeTrue(value != null, "Missing required config: " + key);
        return value;
    }
}
