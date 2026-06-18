package sibudaya.e2e.support;

import org.openqa.selenium.WebDriver;
import sibudaya.e2e.pages.AuthPage;

public final class AuthHelper {
    private AuthHelper() {
    }

    public static void loginAsSuperadmin(WebDriver driver) {
        new AuthPage(driver).loginAsSuperadmin();
    }

    public static void loginAsUser(WebDriver driver) {
        new AuthPage(driver).loginAsUser();
    }
}
