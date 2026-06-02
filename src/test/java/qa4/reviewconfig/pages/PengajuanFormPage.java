package qa4.reviewconfig.pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import qa4.reviewconfig.support.TargetStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PengajuanFormPage extends BasePage {
    private static final Pattern USER_STATUS_URL_PATTERN = Pattern.compile("/dashboard/status/([^/?#]+)");

    public PengajuanFormPage(WebDriver driver) {
        super(driver);
    }

    public String createPentasPengajuan(TargetStatus targetStatus) {
        openStepTwoForm();

        String suffix = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
                + "-" + targetStatus.name().toLowerCase();
        LocalDate startDate = LocalDate.now().plusDays(7);
        LocalDate endDate = startDate.plusDays(1);

        String selectedPaket = "Pentas Seni";
        String namaKegiatan = "QA4 Auto Pentas " + suffix;
        String tujuanKegiatan = "Data uji automation QA4";
        String alamatLengkap = "Jl. QA4 Automation No. 4";
        String kabupatenKota = "Bantul";
        String kecamatan = "Bambanglipuro";
        String kelurahanDesa = "Sidomulyo";
        String kodePos = "55111";

        selectOptionContaining(By.name("selectedPaket"), selectedPaket);
        editTextInput("namaKegiatan", namaKegiatan);
        editTextInput("tujuanKegiatan", tujuanKegiatan);
        setDateInput("tanggalMulai", startDate);
        setDateInput("tanggalSelesai", endDate);
        editTextInput("alamatLengkap", alamatLengkap);
        selectOptionContaining(By.name("kabupatenKota"), kabupatenKota);
        selectOptionContaining(By.name("kecamatan"), kecamatan);
        selectOptionContaining(By.name("kelurahanDesa"), kelurahanDesa);
        editTextInput("kodePos", kodePos);

        persistPentasStepTwoData(
                selectedPaket,
                namaKegiatan,
                tujuanKegiatan,
                startDate,
                endDate,
                alamatLengkap,
                kabupatenKota,
                kecamatan,
                kelurahanDesa,
                kodePos
        );
        openAuthenticatedPath("/dashboard/ajukan-fasilitasi/form/step-3?jenis=1");
        waitForUrlContains("/dashboard/ajukan-fasilitasi/form/step-3");

        fillIfPresent("nomorHp", "081234567890");
        fillIfPresent("email", "qa4.automation@example.com");
        chooseBank("BCA");
        editTextInput("nomorRekening", "1234567890");
        editTextInput("namaPemegangRekening", "QA4 Automation");
        editTextInput("totalDana", "1000000");
        uploadPdfToFirstFileInput();
        editTextInput("alamatLembaga", "Jl. QA4 Automation No. 4, Yogyakarta");

        clickVisibleButton("Kirim Pengajuan");
        assertVisibleAnyText("Pengajuan berhasil dikirim.", "Status Pengajuan", "Dalam Proses", "Pentas");

        return captureNewestPengajuanId();
    }

    private void persistPentasStepTwoData(
            String selectedPaket,
            String namaKegiatan,
            String tujuanKegiatan,
            LocalDate startDate,
            LocalDate endDate,
            String alamatLengkap,
            String kabupatenKota,
            String kecamatan,
            String kelurahanDesa,
            String kodePos
    ) {
        ((JavascriptExecutor) driver).executeScript(
                "const existing = JSON.parse(window.localStorage.getItem('pengajuan_form_data') || '{}');" +
                        "window.localStorage.setItem('pengajuan_form_data', JSON.stringify({" +
                        "...existing," +
                        "selectedPaket: arguments[0]," +
                        "selectedPaketId: ''," +
                        "namaKegiatan: arguments[1]," +
                        "tujuanKegiatan: arguments[2]," +
                        "tanggalMulai: arguments[3]," +
                        "tanggalSelesai: arguments[4]," +
                        "alamatLengkap: arguments[5]," +
                        "alamatLokasi: arguments[5]," +
                        "provinsi: 'Daerah Istimewa Yogyakarta'," +
                        "kabupatenKota: arguments[6]," +
                        "kecamatan: arguments[7]," +
                        "kelurahanDesa: arguments[8]," +
                        "kodePos: arguments[9]" +
                        "}));",
                selectedPaket,
                namaKegiatan,
                tujuanKegiatan,
                startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                endDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                alamatLengkap,
                kabupatenKota,
                kecamatan,
                kelurahanDesa,
                kodePos
        );
    }

    private void openStepTwoForm() {
        String stepTwoPath = "/dashboard/ajukan-fasilitasi/form/step-2?jenis=1";
        TimeoutException lastException = null;
        for (int attempt = 0; attempt < 3; attempt++) {
            openAuthenticatedPath(stepTwoPath);
            waitForUrlContains("/dashboard/ajukan-fasilitasi/form/step-2");
            try {
                pageWait().until(ExpectedConditions.elementToBeClickable(By.name("selectedPaket")));
                assertVisibleText("Detail Kegiatan Pentas");
                return;
            } catch (TimeoutException exception) {
                lastException = exception;
            }
        }
        throw lastException;
    }

    public void submitRevision(String pengajuanId, boolean hibah) {
        String jenisId = hibah ? "2" : "1";
        openAuthenticatedPath("/dashboard/ajukan-fasilitasi/form/step-2?jenis=" + jenisId + "&revisi=" + pengajuanId);

        if (hibah) {
            editTextInput("namaPenerima", "Penerima Revisi QA4");
        } else {
            editTextInput("namaKegiatan", "Kegiatan Revisi QA4");
        }

        clickVisibleButton("Simpan Dan Lanjutkan");
        waitForUrlContains("/dashboard/ajukan-fasilitasi/form/step-3");

        WebElement fileInput = driver.findElement(By.cssSelector("input[type='file'][accept*='pdf']"));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].classList.remove('hidden'); arguments[0].style.display = 'block';",
                fileInput
        );
        fileInput.sendKeys(proposalPath().toString());

        pageWait().until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "proposal-revisi-sample.pdf"));
        clickVisibleButton("Kirim Revisi");
    }

    private void chooseBank(String query) {
        WebElement input = pageWait().until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[role='combobox']")));
        input.clear();
        input.sendKeys(query);
        WebElement listBox = pageWait().until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[role='listbox']")));
        WebElement option = listBox.findElement(By.cssSelector("[role='option']"));
        option.click();
        pageWait().until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[role='listbox']")));
    }

    private String captureNewestPengajuanId() {
        openAuthenticatedPath("/dashboard");
        WebElement statusLink = pageWait().until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='/dashboard/status/']")));
        String href = statusLink.getAttribute("href");
        Matcher matcher = USER_STATUS_URL_PATTERN.matcher(href);
        Assertions.assertTrue(matcher.find(), "Could not capture pengajuan id from status link: " + href);
        return matcher.group(1);
    }
}
