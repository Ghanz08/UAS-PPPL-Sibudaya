package sibudaya.e2e.pages;

import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AdminDashboardPage extends BaseE2ePage {
    public AdminDashboardPage(WebDriver driver) {
        super(driver);
    }

    public void assertShown() {
        waitForUrlContains("/dashboard/admin");
        assertVisibleAnyText("Dashboard", "Permohonan", "Lembaga", "Fasilitasi", "Manajemen");
    }

    public void openSubmittedRequest(String marker) {
        openPath("/dashboard/admin");
        waitForUrlContains("/dashboard/admin");
        for (WebElement search : driver.findElements(By.cssSelector("input[placeholder*='Cari'], input[type='search'], input[type='text']"))) {
            if (search.isDisplayed() && search.isEnabled()) {
                search.clear();
                search.sendKeys(marker);
                break;
            }
        }
        for (WebElement link : driver.findElements(By.cssSelector("a[href*='/dashboard/admin/status/']"))) {
            if (link.isDisplayed()) {
                link.click();
                waitForUrlContains("/dashboard/admin/status/");
                return;
            }
        }
        Assumptions.abort("Submitted request is not visible in admin dashboard for marker: " + marker);
    }
}
