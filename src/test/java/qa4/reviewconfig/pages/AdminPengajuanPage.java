package qa4.reviewconfig.pages;

import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class AdminPengajuanPage extends BasePage {
    public AdminPengajuanPage(WebDriver driver) {
        super(driver);
    }

    public void openAdminPengajuanDetail(String pengajuanId) {
        openAuthenticatedPath("/dashboard/admin/status/" + pengajuanId);
        assertVisibleAnyText("Status Pengajuan Fasilitasi", "Status Review Admin");
    }

    public void requestTimelineRevision(String reason) {
        clickVisibleButton("Perlu Revisi");
        WebElement textarea = pageWait().until(ExpectedConditions.elementToBeClickable(
                By.xpath("//textarea[@placeholder='Tulis catatan revisi']")
        ));
        textarea.clear();
        textarea.sendKeys(reason);
        clickVisibleButton("Konfirmasi Revisi");
        waitForBodyText("Perlu Revisi");
    }

    public void rejectTimeline(String reason) {
        clickVisibleButton("Tolak");
        WebElement textarea = pageWait().until(ExpectedConditions.elementToBeClickable(
                By.xpath("//textarea[@placeholder='Tulis alasan penolakan']")
        ));
        textarea.clear();
        textarea.sendKeys(reason);
        clickVisibleButton("Konfirmasi Tolak");
        waitForBodyText("Ditolak");
    }

    public void completePentasPengajuan() {
        approvePemeriksaanPentas();
        uploadAdminTimelineFile("Unggah Berkas");
        approveCurrentTimelineStep();
    }

    public void finishPencairan() {
        uploadAdminTimelineFile("Unggah Berkas");
        clickVisibleButton("Selesaikan Pencairan");
        assertVisibleText("Selesai");
    }

    public void approveCurrentTimelineStep() {
        clickVisibleButton("Setujui");
        waitUntilNotLoading();
    }

    public void openNotifications() {
        openAuthenticatedPath("/dashboard/admin");
        WebElement button = pageWait().until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[aria-label='Notifikasi']")));
        button.click();
        assertVisibleText("Notifications");
    }

    private void approvePemeriksaanPentas() {
        clickVisibleButton("Setujui");
        waitForBodyText("Pilih Paket");
        List<WebElement> packageButtons = driver.findElements(By.xpath(
                "//div[contains(@class, 'fixed')]//button[not(@disabled) and .//span[contains(normalize-space(.), 'Rp')]]"
        ));
        Assumptions.assumeTrue(!packageButtons.isEmpty(), "No selectable package available for auto-created QA4 pengajuan.");
        packageButtons.get(0).click();
        clickVisibleButton("Pilih Paket");
        waitForBodyText("Pengisian dan Penandatangan Surat Persetujuan");
    }

    private void uploadAdminTimelineFile(String buttonLabel) {
        clickVisibleButton(buttonLabel);
        uploadPdfToFirstFileInput();
        waitForBodyText("proposal-revisi-sample.pdf");
        waitUntilNotLoading();
    }
}
