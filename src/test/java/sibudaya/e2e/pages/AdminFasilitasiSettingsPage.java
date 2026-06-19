package sibudaya.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import sibudaya.e2e.support.E2eTestData;
import sibudaya.e2e.support.FasilitasiType;

public class AdminFasilitasiSettingsPage extends BaseE2ePage {
    public AdminFasilitasiSettingsPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        openPath("/dashboard/admin/pengaturan-fasilitasi");
        waitForUrlContains("/dashboard/admin/pengaturan-fasilitasi");
        assertVisibleAnyText("General", "Pentas", "Hibah", "Pengaturan Fasilitasi");
    }

    public void performCrud(FasilitasiType type) {
        openTab(type);
        String marker = shortMarker(type);
        String updatedMarker = marker + "-EDIT";

        log("START CRUD paket fasilitasi: type=" + type.label() + ", jenisId=" + type.jenisId()
                + ", marker=" + marker + ", updatedMarker=" + updatedMarker);
        createJenis(type, marker);
        readJenisInUi(type, marker);
        updateJenis(type, marker, updatedMarker);
        readJenisInUi(type, updatedMarker);
        deleteJenis(type, updatedMarker);
        assertJenisDeleted(type, updatedMarker);
        log("FINISH CRUD paket fasilitasi via UI: type=" + type.label() + ", marker=" + marker
                + ", updatedMarker=" + updatedMarker);
    }

    private void openTab(FasilitasiType type) {
        assertVisibleAnyText("General", "Pentas", "Hibah");
        String label = type == FasilitasiType.PENTAS ? "Pentas" : "Hibah";
        clickFirstVisibleTextLinkOrButton(label);
        assertVisibleText("Jenis Fasilitasi");
        log("Opened fasilitasi tab via UI: type=" + type.label());
    }

    private void createJenis(FasilitasiType type, String marker) {
        assertVisibleAnyText("Jenis Fasilitasi", "Kuota Pengajuan", "Tambah Jenis");
        clickFirstVisibleTextLinkOrButton("Tambah Jenis", "Tambah Paket", "Tambah Fasilitasi");
        String dialogTitle = type == FasilitasiType.HIBAH ? "Tambah Jenis Fasilitasi Hibah" : "Tambah Jenis Fasilitasi";
        assertVisibleText(dialogTitle);
        typeByFirstLabel(marker, "Jenis Fasilitasi", "Nama Paket", "Nama Fasilitasi");
        typeByFirstLabel("9", "Kuota Pengajuan", "Kuota");
        if (type == FasilitasiType.PENTAS) {
            typeByFirstLabel("15000000", "Dana Pembinaan", "Nilai Bantuan", "Nominal Bantuan");
        }
        selectByFirstLabel("BERKALI_KALI", "Aturan Pengajuan", "Frekuensi Pengajuan");
        clickDialogButton(dialogTitle, "Tambah Jenis", "Simpan", "Tambah");
        waitForDialogClosed(dialogTitle);
        waitForAnySuccessText(type == FasilitasiType.HIBAH
                ? "Jenis fasilitasi hibah berhasil ditambahkan."
                : "Jenis fasilitasi berhasil ditambahkan.");
        log("SUCCESS create fasilitasi via UI: type=" + type.label() + ", nama=" + marker
                + ", kuota=9, nilai_bantuan=" + (type == FasilitasiType.PENTAS ? "15000000" : "null")
                + ", aturan=BERKALI_KALI");
    }

    private void readJenisInUi(FasilitasiType type, String marker) {
        openTab(type);
        assertVisibleText(marker);
        log("SUCCESS read fasilitasi via UI: type=" + type.label() + ", nama=" + marker);
    }

    private void updateJenis(FasilitasiType type, String currentMarker, String updatedMarker) {
        clickMarkerRowAction(currentMarker, "Edit");
        String dialogTitle = type == FasilitasiType.HIBAH ? "Edit Jenis Fasilitasi Hibah" : "Edit Jenis Fasilitasi";
        assertVisibleText(dialogTitle);
        typeByFirstLabel(updatedMarker, "Jenis Fasilitasi", "Nama Paket", "Nama Fasilitasi");
        typeByFirstLabel("11", "Kuota Pengajuan", "Kuota");
        clickDialogButton(dialogTitle, "Simpan Perubahan", "Simpan");
        waitForDialogClosed(dialogTitle);
        waitForAnySuccessText(type == FasilitasiType.HIBAH
                ? "Jenis fasilitasi hibah berhasil diperbarui."
                : "Jenis fasilitasi berhasil diperbarui.");
        log("SUCCESS update fasilitasi via UI: from=" + currentMarker + ", to=" + updatedMarker + ", kuota=11");
    }

    private void deleteJenis(FasilitasiType type, String marker) {
        clickMarkerRowAction(marker, "Hapus");
        waitForAnySuccessText(type == FasilitasiType.HIBAH
                ? "Jenis fasilitasi hibah berhasil dihapus."
                : "Jenis fasilitasi berhasil dihapus.");
        log("SUCCESS delete fasilitasi via UI: nama=" + marker);
    }

    private void assertJenisDeleted(FasilitasiType type, String marker) {
        waitForPage().until(webDriver -> webDriver.findElements(textLocator(marker)).stream().noneMatch(WebElement::isDisplayed));
        log("SUCCESS verify fasilitasi deleted via UI: nama=" + marker);
    }

    private void clickMarkerRowAction(String marker, String action) {
        String markerLiteral = xpathLiteral(marker);
        String actionLiteral = xpathLiteral(action);
        By locator = By.xpath(
                "//*[not(*) and contains(normalize-space(.), " + markerLiteral + ")]" +
                        "/ancestor::*[.//button[contains(normalize-space(.), " + actionLiteral + ")]][1]" +
                        "//button[contains(normalize-space(.), " + actionLiteral + ")]"
        );
        for (WebElement button : driver.findElements(locator)) {
            if (button.isDisplayed() && button.isEnabled()) {
                clickElement(button);
                return;
            }
        }
        throw new AssertionError("Could not click " + action + " for fasilitasi marker " + marker
                + System.lineSeparator() + visibleText());
    }

    private void typeByFirstLabel(String value, String... labels) {
        RuntimeException lastRuntime = null;
        AssertionError lastAssertion = null;
        for (String label : labels) {
            try {
                typeByLabel(label, value);
                return;
            } catch (AssertionError failure) {
                lastAssertion = failure;
            } catch (RuntimeException failure) {
                lastRuntime = failure;
            }
        }
        if (lastAssertion != null) {
            if (lastRuntime != null) lastAssertion.addSuppressed(lastRuntime);
            throw lastAssertion;
        }
        if (lastRuntime != null) {
            throw lastRuntime;
        }
        throw new AssertionError("No matching label for value " + value);
    }

    private void selectByFirstLabel(String value, String... labels) {
        RuntimeException lastRuntime = null;
        AssertionError lastAssertion = null;
        for (String label : labels) {
            try {
                selectByLabel(label, value);
                return;
            } catch (AssertionError failure) {
                lastAssertion = failure;
            } catch (RuntimeException failure) {
                lastRuntime = failure;
            }
        }
        if (lastAssertion != null) {
            if (lastRuntime != null) lastAssertion.addSuppressed(lastRuntime);
            throw lastAssertion;
        }
        if (lastRuntime != null) {
            throw lastRuntime;
        }
        throw new AssertionError("No matching select label for value " + value);
    }

    private String shortMarker(FasilitasiType type) {
        String raw = E2eTestData.safeId().replaceAll("[^a-zA-Z0-9]", "");
        String suffix = raw.substring(Math.max(0, raw.length() - 8));
        return "AUTO-" + type.label().toUpperCase() + "-" + suffix;
    }

    private void clickDialogButton(String title, String... labels) {
        String titleLiteral = xpathLiteral(title);
        for (String label : labels) {
            String labelLiteral = xpathLiteral(label);
            for (WebElement button : driver.findElements(By.xpath(
                    "//dialog[.//*[contains(normalize-space(.), " + titleLiteral + ")]]//button[contains(normalize-space(.), " + labelLiteral + ")]"
            ))) {
                if (button.isDisplayed() && button.isEnabled()) {
                    clickElement(button);
                    return;
                }
            }
        }
        throw new AssertionError("Could not click dialog button for " + title + System.lineSeparator() + visibleText());
    }

    private void waitForDialogClosed(String title) {
        String titleLiteral = xpathLiteral(title);
        waitForPage().until(webDriver -> webDriver.findElements(By.xpath(
                "//dialog[@open and .//*[contains(normalize-space(.), " + titleLiteral + ")]]"
        )).stream().noneMatch(WebElement::isDisplayed));
    }

    private void log(String message) {
        System.out.println("[E2E CRUD][Fasilitasi Settings] " + message);
    }
}
