package sibudaya.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import sibudaya.e2e.support.E2eTestData;
import shared.utils.WaitHelper;

public class UserProfilePage extends BaseE2ePage {
    public UserProfilePage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        openPath("/dashboard/my-profile");
        waitForUrlContains("/dashboard/my-profile");
        assertVisibleAnyText("Profil", "Data Lembaga", "Data Kepala Lembaga");
    }

    public void updateKepalaLembagaFirstName() {
        clickVisibleTextLinkOrButton("Kepala Lembaga");
        assertVisibleText("Data Kepala Lembaga");
        String newName = "Kepala" + E2eTestData.marker().replace("AUTO-E2E-", "");
        typeKepalaField("Nama Depan", newName);
        clickVisibleTextLinkOrButton("Simpan Data Kepala");
        waitForAnySuccessText("Data kepala lembaga berhasil diperbarui", "berhasil diperbarui", newName);
        assertVisibleAnyText(newName, "Data Kepala Lembaga");
    }

    private void typeKepalaField(String label, String value) {
        WebElement input = waitForPage().until(webDriver -> webDriver.findElements(By.xpath(
                        "//form[contains(normalize-space(.), 'Data Kepala Lembaga')]//*[normalize-space(.)=" + xpathLiteral(label) + "]/ancestor::div[1]//input"
                )).stream()
                .filter(WebElement::isDisplayed)
                .filter(WebElement::isEnabled)
                .findFirst()
                .orElse(null));
        WaitHelper.pauseForVisual();
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(value);
    }
}
