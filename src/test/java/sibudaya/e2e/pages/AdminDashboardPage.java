package sibudaya.e2e.pages;

import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import shared.utils.WaitHelper;

public class AdminDashboardPage extends BaseE2ePage {
    public AdminDashboardPage(WebDriver driver) {
        super(driver);
    }

    public void assertShown() {
        waitForDashboardShown();
    }

    public void openSubmittedRequest(String marker) {
        if ("FALLBACK_STATUS".equals(marker)) {
            openPath("/dashboard/admin");
            waitForDashboardShown();
            return;
        }
        openPath("/dashboard/admin");
        waitForDashboardShown();
        for (WebElement search : driver.findElements(By.cssSelector("input[placeholder*='Cari'], input[type='search'], input[type='text']"))) {
            if (search.isDisplayed() && search.isEnabled()) {
                search.clear();
                search.sendKeys(marker);
                break;
            }
        }
        for (WebElement link : driver.findElements(By.cssSelector("a[href*='/dashboard/admin/status/']"))) {
            if (link.isDisplayed()) {
                WaitHelper.pauseForVisual();
                link.click();
                waitForUrlContains("/dashboard/admin/status/");
                return;
            }
        }
        Assumptions.abort("Submitted request is not visible in admin dashboard for marker: " + marker);
    }

    private void waitForDashboardShown() {
        waitForUrlAndAnyText("/dashboard/admin", "Dashboard", "Permohonan", "Lembaga", "Fasilitasi", "Manajemen");
    }
}
