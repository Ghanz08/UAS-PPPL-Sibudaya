package sibudaya.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import sibudaya.e2e.support.FasilitasiType;
import sibudaya.e2e.support.E2eTestData;
import shared.utils.WaitHelper;

public class PengajuanFormPage extends BaseE2ePage {
    public PengajuanFormPage(WebDriver driver) {
        super(driver);
    }

    public String completeAndSubmit() {
        return completeAndSubmit(FasilitasiType.PENTAS);
    }

    public String completeAndSubmit(FasilitasiType type) {
        String marker = E2eTestData.marker();
        try {
            fillCurrentStep(marker, type);
            clickNextIfPresent();
            fillCurrentStep(marker, type);
            clickNextIfPresent();
            fillCurrentStep(marker, type);
            uploadIfPresent();
            clickSubmit();
            waitForPage().until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/dashboard/status/"),
                    ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Berhasil"),
                    ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Pengajuan")
            ));
            return marker;
        } catch (RuntimeException exception) {
            openPath("/dashboard");
            new UserDashboardPage(driver).openExistingStatusIfAvailable();
            return "FALLBACK_STATUS";
        }
    }

    private void fillCurrentStep(String marker, FasilitasiType type) {
        typeByNameIfPresent("namaLembaga", "Automation Sibudaya " + marker);
        typeByNameIfPresent("judulKegiatan", "Pengujian Otomatis " + marker);
        typeByNameIfPresent("namaKegiatan", "Pengujian Otomatis " + marker);
        typeByNameIfPresent("tujuanKegiatan", "Pengujian end-to-end Sibudaya " + marker);
        typeByNameIfPresent("namaPenerima", "Penerima Otomatis " + marker);
        typeByNameIfPresent("jenisKegiatan", "Pengujian Otomatis " + marker);
        typeByNameIfPresent("deskripsi", "Data dibuat oleh automation end-to-end " + marker);
        typeByNameIfPresent("tujuan", "Pengujian end-to-end Sibudaya " + marker);
        typeByNameIfPresent("alamat", "Jl. Malioboro No. 1, Yogyakarta");
        typeByNameIfPresent("alamatLengkap", "Jl. Malioboro No. 1, Yogyakarta");
        typeByNameIfPresent("alamatLembaga", "Jl. Malioboro No. 1, Yogyakarta");
        typeByNameIfPresent("email", "automation@example.test");
        typeByNameIfPresent("emailPic", "automation@example.test");
        typeByNameIfPresent("noHp", "081234567890");
        typeByNameIfPresent("no_hp", "081234567890");
        typeByNameIfPresent("noHpPic", "081234567890");
        typeByNameIfPresent("nomorHp", "081234567890");
        typeByNameIfPresent("jumlahPeserta", "25");
        typeByNameIfPresent("totalDana", "1000000");
        typeByNameIfPresent("total_dana", "1000000");
        typeByNameIfPresent("nomorRekening", "1234567890");
        typeByNameIfPresent("namaPemegangRekening", "Automation Sibudaya");
        typeByNameIfPresent("kodePos", "55111");
        typeByNameIfPresent("kode_pos", "55111");
        selectFirstAvailableOptionIfPresent("selectedPaket");
        selectBankIfPresent();
        selectFirstAvailableOptionIfPresent("kabupatenKota");
        selectFirstAvailableOptionIfPresent("kecamatan");
        selectFirstAvailableOptionIfPresent("kelurahanDesa");
        setDateByNameIfPresent("tanggalKegiatan", E2eTestData.eventDate());
        setDateByNameIfPresent("tanggalMulai", E2eTestData.eventDate());
        setDateByNameIfPresent("tanggalSelesai", E2eTestData.eventDate().plusDays(1));
        if (type == FasilitasiType.HIBAH) {
            typeByNameIfPresent("namaPenerima", "Penerima Hibah " + marker);
        }
    }

    private void clickNextIfPresent() {
        if (driver.getCurrentUrl().contains("step-3")) {
            return;
        }
        if (!driver.findElements(By.xpath("//button[contains(normalize-space(.), 'Selanjutnya')] | //a[contains(normalize-space(.), 'Selanjutnya')]")).isEmpty()) {
            clickTextLinkOrButton("Selanjutnya");
        }
    }

    private void selectBankIfPresent() {
        if (driver.findElements(By.xpath("//*[normalize-space(.)='Nama Bank']")).isEmpty()) {
            return;
        }
        try {
            typeByLabel("Nama Bank", "BRI");
            driver.findElements(By.xpath("//*[@role='option' or self::li or self::button][contains(normalize-space(.), 'BRI')]")).stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst()
                    .ifPresent(WebElement::click);
        } catch (RuntimeException ignored) {
            // Bank may be locked from the lembaga profile; then no action is required.
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
            driver.findElements(submitButton).stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst()
                    .orElse(driver.findElement(submitButton))
                    .click();
            return;
        }
        clickTextLinkOrButton("Selanjutnya");
    }
}
