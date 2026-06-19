package sibudaya.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import sibudaya.e2e.support.AuthHelper;
import shared.utils.WaitHelper;

public class UserDashboardPage extends BaseE2ePage {
    public UserDashboardPage(WebDriver driver) {
        super(driver);
    }

    public void assertShown() {
        waitForDashboardShown();
    }

    public void openFacilitationSelection() {
        openPath("/dashboard/ajukan-fasilitasi");
        waitForUrlContains("/dashboard/ajukan-fasilitasi");
    }

    public void openExistingStatusIfAvailable() {
        openPath("/dashboard");
        try {
            waitForDashboardShown();
        } catch (AssertionError | TimeoutException failure) {
            if (!driver.getCurrentUrl().contains("/login")) {
                throw failure;
            }
            AuthHelper.loginAsUser(driver);
            openPath("/dashboard");
            waitForDashboardShown();
        }
        for (WebElement link : driver.findElements(By.cssSelector("a[href*='/dashboard/status/']"))) {
            if (link.isDisplayed()) {
                WaitHelper.pauseForVisual();
                link.click();
                waitForUrlContains("/dashboard/status/");
                return;
            }
        }
    }

    public void openProfile() {
        openPath("/dashboard/my-profile");
    }

    public void assertProfileOrDashboardFallbackShown() {
        assertVisibleAnyText("Profil", "Data Lembaga", "Dashboard", "Pantau perkembangan");
    }

    private void waitForDashboardShown() {
        waitForUrlAndAnyText("/dashboard", "Dashboard", "Pantau perkembangan", "Ajukan Fasilitasi", "Riwayat Fasilitasi");
    }
}

