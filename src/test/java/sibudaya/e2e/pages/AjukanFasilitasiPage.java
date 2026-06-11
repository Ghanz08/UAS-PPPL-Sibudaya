package sibudaya.e2e.pages;

import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AjukanFasilitasiPage extends BaseE2ePage {
    public AjukanFasilitasiPage(WebDriver driver) {
        super(driver);
    }

    public void assertShownReadOnly() {
        waitForUrlContains("/dashboard/ajukan-fasilitasi");
        assertVisibleAnyText("Pilih jenis fasilitasi", "Fasilitasi", "Pentas", "Hibah", "Sarana");
    }

    public void startFirstAvailableSubmission() {
        for (WebElement link : driver.findElements(By.cssSelector("a[href*='/dashboard/ajukan-fasilitasi/form']"))) {
            if (link.isDisplayed() && link.isEnabled() && !link.getText().toLowerCase().contains("tidak tersedia")) {
                link.click();
                waitForUrlContains("/dashboard/ajukan-fasilitasi/form");
                return;
            }
        }
        Assumptions.abort("No available facilitation submission link is visible for this account.");
    }
}
