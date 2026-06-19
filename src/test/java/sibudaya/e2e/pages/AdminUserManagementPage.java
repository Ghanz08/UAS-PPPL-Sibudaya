package sibudaya.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import sibudaya.e2e.support.E2eTestData;
import shared.utils.WaitHelper;

public class AdminUserManagementPage extends BaseE2ePage {
    public AdminUserManagementPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        openPath("/dashboard/admin/pengaturan-akun");
        waitForUrlContains("/dashboard/admin/pengaturan-akun");
        assertVisibleText("Manajemen Pengguna");
    }

    public void performCrud() {
        String suffix = E2eTestData.safeId().replaceAll("[^a-zA-Z0-9]", "");
        String firstName = "Auto" + suffix;
        String updatedFirstName = "AutoEdit" + suffix;
        String email = E2eTestData.uniqueEmail("admin");
        String phone = E2eTestData.uniquePhone();

        assertVisibleText("Manajemen Pengguna");
        log("START CRUD admin user with data: firstName=" + firstName + ", updatedFirstName=" + updatedFirstName
                + ", email=" + email + ", phone=" + phone);
        createAdmin(firstName, email, phone);
        readAdminInUi(firstName, email);
        updateFirstName(updatedFirstName);
        backToList();
        readAdminInUi(updatedFirstName, email);
        backToList();
        search(updatedFirstName);
        deleteAdmin(updatedFirstName);
        assertAdminDeleted(updatedFirstName);
        log("FINISH CRUD admin user via UI: email=" + email);
    }

    private void createAdmin(String firstName, String email, String phone) {
        clickVisibleTextLinkOrButton("Tambahkan Admin");
        assertVisibleText("Registrasi Akun Admin");
        typeByPlaceholder("Nama depan", firstName);
        typeByPlaceholder("Nama belakang", "Automation");
        typeByPlaceholder("Email", email);
        typeByPlaceholder("Nomor Hp", phone);
        typeByPlaceholder("Alamat", "Jl. Malioboro No. 1, Yogyakarta");
        typeByPlaceholder("Password", "AdminAuto@2026!");
        typeByPlaceholder("Konfirmasi password", "AdminAuto@2026!");
        clickVisibleTextLinkOrButton("Daftarkan");
        waitForPage().until(webDriver -> webDriver.findElements(By.xpath("//*[contains(normalize-space(.), 'Registrasi Akun Admin')]")).stream().noneMatch(WebElement::isDisplayed));
        log("SUCCESS create admin via UI: firstName=" + firstName + ", lastName=Automation, email=" + email
                + ", phone=" + phone + ", address=Jl. Malioboro No. 1, Yogyakarta");
    }

    private void readAdminInUi(String expectedName, String email) {
        search(expectedName);
        assertVisibleText(expectedName);
        assertVisibleAnyText(email, "Automation");
        log("SUCCESS read admin via UI list: name=" + expectedName + ", email=" + email);
        openDetail(expectedName);
        assertVisibleAnyText(email, expectedName, "Automation");
        log("SUCCESS read admin via UI detail: email=" + email + ", expectedName=" + expectedName);
    }

    private void backToList() {
        clickFirstVisibleTextLinkOrButton("Kembali ke daftar", "Kembali");
        assertVisibleText("Manajemen Pengguna");
    }

    private void search(String query) {
        WebElement search = waitForPage().until(webDriver -> webDriver.findElements(By.cssSelector("input[placeholder*='Cari']")).stream()
                .filter(WebElement::isDisplayed)
                .filter(WebElement::isEnabled)
                .findFirst()
                .orElse(null));
        ((JavascriptExecutor) driver).executeScript(
                "const input = arguments[0]; const value = arguments[1];" +
                        "input.scrollIntoView({block:'center', inline:'nearest'});" +
                        "input.focus({preventScroll:true});" +
                        "const setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                        "setter.call(input, value);" +
                        "input.dispatchEvent(new Event('input', {bubbles:true}));" +
                        "input.dispatchEvent(new Event('change', {bubbles:true}));",
                search,
                query
        );
        WaitHelper.pauseForVisual();
    }

    private void openDetail(String name) {
        clickVisibleTextInScope(name, "Detail");
        assertVisibleText("Informasi Akun");
    }

    private void updateFirstName(String firstName) {
        typeByLabel("Nama Depan", firstName);
        clickVisibleTextLinkOrButton("Edit Informasi Akun");
        waitForAnySuccessText("berhasil", firstName);
        log("SUCCESS update admin via UI: firstName=" + firstName);
    }

    private void deleteAdmin(String name) {
        try {
            clickVisibleTextInScope(name, "Hapus");
        } catch (AssertionError ignored) {
            WebElement row = waitForPage().until(webDriver -> webDriver.findElements(By.xpath("//*[contains(normalize-space(.), " + xpathLiteral(name) + ")]//ancestor::*[self::article or self::div][.//button][1]")).stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst()
                    .orElse(null));
            row.findElements(By.cssSelector("button")).stream()
                    .filter(WebElement::isDisplayed)
                    .reduce((first, second) -> second)
                    .orElseThrow(() -> new AssertionError("Delete button not found for " + name))
                    .click();
        }
        assertVisibleText("Hapus Akun");
        clickVisibleTextLinkOrButton("Hapus");
        log("SUCCESS delete admin via UI: name=" + name);
    }

    private void assertAdminDeleted(String name) {
        search(name);
        By rowLocator = By.xpath("//*[contains(normalize-space(.), " + xpathLiteral(name) + ")]"
                + "[.//button[contains(normalize-space(.), 'Detail') or contains(normalize-space(.), 'Hapus') or contains(@aria-label, 'Hapus')]]");
        waitForPage().until(webDriver -> webDriver.findElements(rowLocator).stream().noneMatch(WebElement::isDisplayed));
        log("SUCCESS verify admin deleted via UI: name=" + name);
    }

    private void typeByPlaceholder(String placeholder, String value) {
        WebElement element = waitForPage().until(webDriver -> webDriver.findElements(By.cssSelector("input[placeholder='" + placeholder + "']")).stream()
                .filter(WebElement::isDisplayed)
                .filter(WebElement::isEnabled)
                .findFirst()
                .orElse(null));
        element.click();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(value);
    }

    private void log(String message) {
        System.out.println("[E2E CRUD][Admin User] " + message);
    }
}
