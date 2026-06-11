package sibudaya.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import sibudaya.e2e.support.E2eTestData;

public class PengajuanFormPage extends BaseE2ePage {
    public PengajuanFormPage(WebDriver driver) {
        super(driver);
    }

    public String completeAndSubmit() {
        String marker = E2eTestData.marker();
        fillCurrentStep(marker);
        clickNextIfPresent();
        fillCurrentStep(marker);
        clickNextIfPresent();
        fillCurrentStep(marker);
        uploadIfPresent();
        clickSubmit();
        waitForPage().until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/dashboard/status/"),
                ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Berhasil"),
                ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Pengajuan")
        ));
        return marker;
    }

    private void fillCurrentStep(String marker) {
        typeByNameIfPresent("namaLembaga", "Automation Sibudaya " + marker);
        typeByNameIfPresent("judulKegiatan", "Pengujian Otomatis " + marker);
        typeByNameIfPresent("namaKegiatan", "Pengujian Otomatis " + marker);
        typeByNameIfPresent("jenisKegiatan", "Pengujian Otomatis " + marker);
        typeByNameIfPresent("deskripsi", "Data dibuat oleh automation end-to-end " + marker);
        typeByNameIfPresent("tujuan", "Pengujian end-to-end Sibudaya " + marker);
        typeByNameIfPresent("alamat", "Jl. Malioboro No. 1, Yogyakarta");
        typeByNameIfPresent("alamatLembaga", "Jl. Malioboro No. 1, Yogyakarta");
        typeByNameIfPresent("email", "automation@example.test");
        typeByNameIfPresent("emailPic", "automation@example.test");
        typeByNameIfPresent("noHp", "081234567890");
        typeByNameIfPresent("no_hp", "081234567890");
        typeByNameIfPresent("noHpPic", "081234567890");
        typeByNameIfPresent("jumlahPeserta", "25");
        typeByNameIfPresent("totalDana", "1000000");
        typeByNameIfPresent("total_dana", "1000000");
        typeByNameIfPresent("kodePos", "55111");
        typeByNameIfPresent("kode_pos", "55111");
        selectFirstAvailableOptionIfPresent("selectedPaket");
        selectFirstAvailableOptionIfPresent("kabupatenKota");
        selectFirstAvailableOptionIfPresent("kecamatan");
        selectFirstAvailableOptionIfPresent("kelurahanDesa");
        setDateByNameIfPresent("tanggalKegiatan", E2eTestData.eventDate());
        setDateByNameIfPresent("tanggalMulai", E2eTestData.eventDate());
        setDateByNameIfPresent("tanggalSelesai", E2eTestData.eventDate().plusDays(1));
    }

    private void clickNextIfPresent() {
        if (!driver.findElements(By.xpath("//button[contains(normalize-space(.), 'Selanjutnya')] | //a[contains(normalize-space(.), 'Selanjutnya')]")).isEmpty()) {
            clickTextLinkOrButton("Selanjutnya");
        }
    }

    private void uploadIfPresent() {
        if (!driver.findElements(By.cssSelector("input[type='file']")).isEmpty()) {
            uploadPdf(E2eTestData.proposalPdfPath());
        }
    }

    private void clickSubmit() {
        By submitButton = By.xpath("//button[contains(normalize-space(.), 'Ajukan')] | //button[contains(normalize-space(.), 'Kirim')] | //button[contains(normalize-space(.), 'Submit')]");
        if (!driver.findElements(submitButton).isEmpty()) {
            WaitHelper.pauseForVisual();
            driver.findElement(submitButton).click();
            return;
        }
        clickTextLinkOrButton("Selanjutnya");
    }
}
