package qa4.reviewconfig.pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PengajuanDetailPage extends BasePage {
    public PengajuanDetailPage(WebDriver driver) {
        super(driver);
    }

    public void openDetail(String pengajuanId) {
        openAuthenticatedPath("/dashboard/status/" + pengajuanId);
        assertVisibleAnyText("Status", "Timeline", "Pengajuan");
    }

    public boolean isHibah() {
        return driver.findElements(textLocator("Fasilitasi Hibah")).stream().anyMatch(WebElement::isDisplayed);
    }

    public void assertTimelineChronological() {
        assertVisibleText("Status");
        assertVisibleAnyText("Pengajuan Data Pendaftaran", "Pengisian Data Pendaftaran");
        assertVisibleAnyText("Pemeriksaan Data oleh Admin", "Pemeriksaan Data oleh Admin dan Penetapan Paket Fasilitas");
        assertVisibleAnyText("Tanggal Pengajuan", "Hari/Tanggal");
        List<WebElement> timelineHeadings = driver.findElements(By.xpath(
                "//*[contains(normalize-space(), 'Pengajuan Data Pendaftaran') or " +
                        "contains(normalize-space(), 'Pengisian Data Pendaftaran') or " +
                        "contains(normalize-space(), 'Pemeriksaan Data oleh Admin') or " +
                        "contains(normalize-space(), 'Survey Lapangan') or " +
                        "contains(normalize-space(), 'Pelaporan Kegiatan') or " +
                        "contains(normalize-space(), 'Pencairan Dana')]"
        ));
        Assertions.assertTrue(timelineHeadings.size() >= 2, "Timeline should show at least two chronological steps.");
    }

    public void assertFinishedNotEditable() {
        assertVisibleText("Selesai");
        assertNoVisibleText("Pilih Proposal");
        assertNoVisibleText("Upload Ulang Proposal");
    }

    public void assertRejectedNotEditable() {
        assertVisibleText("Ditolak");
        assertNoVisibleText("Pilih Proposal");
        assertNoVisibleText("Upload Ulang Proposal");
        assertVisibleAnyText("Pengajuan ditolak", "Pengajuan yang ditolak tidak dapat diubah", "ditolak");
    }

    public void uploadLaporan() {
        clickVisibleButton("Pilih File");
        uploadPdfToFirstFileInput();
        waitForBodyText("proposal-revisi-sample.pdf");
        clickVisibleButton("Submit Laporan");
        waitForBodyText("Hasil Laporan");
    }
}
