package sibudaya.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UserDashboardPage extends BaseE2ePage {
    public UserDashboardPage(WebDriver driver) {
        super(driver);
    }

    public void assertShown() {
        waitForUrlContains("/dashboard");
        assertVisibleAnyText("Dashboard", "Pantau perkembangan", "Ajukan Fasilitasi", "Riwayat Fasilitasi");
    }

    public void openFacilitationSelection() {
        openPath("/dashboard/ajukan-fasilitasi");
        waitForUrlContains("/dashboard/ajukan-fasilitasi");
    }

    public void openExistingStatusIfAvailable() {
        openPath("/dashboard");
        waitForUrlContains("/dashboard");
        for (WebElement link : driver.findElements(By.cssSelector("a[href*='/dashboard/status/']"))) {
            if (link.isDisplayed()) {
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
}

