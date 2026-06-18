package sibudaya.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import sibudaya.e2e.support.FasilitasiType;
import shared.utils.WaitHelper;

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
                WaitHelper.pauseForVisual();
                link.click();
                waitForUrlContains("/dashboard/ajukan-fasilitasi/form");
                return;
            }
        }
        openPath("/dashboard/ajukan-fasilitasi/form/step-2?jenis=1");
        waitForUrlContains("/dashboard/ajukan-fasilitasi/form/step-2");
    }

    public void startSubmission(FasilitasiType type) {
        String hrefPart = "/dashboard/ajukan-fasilitasi/form/step-2?jenis=" + type.jenisId();
        for (WebElement link : driver.findElements(By.cssSelector("a[href*='" + hrefPart + "']"))) {
            if (link.isDisplayed() && link.isEnabled()) {
                WaitHelper.pauseForVisual();
                link.click();
                waitForUrlContains("/dashboard/ajukan-fasilitasi/form/step-2");
                return;
            }
        }
        openPath("/dashboard/ajukan-fasilitasi/form/step-2?jenis=" + type.jenisId());
        waitForUrlContains("/dashboard/ajukan-fasilitasi/form/step-2");
    }
}
