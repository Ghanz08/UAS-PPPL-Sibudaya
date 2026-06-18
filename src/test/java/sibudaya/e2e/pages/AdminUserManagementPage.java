package sibudaya.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import sibudaya.e2e.support.E2eTestData;
import shared.utils.WaitHelper;

import java.util.Map;

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
        String suffix = E2eTestData.marker().replace("AUTO-E2E-", "");
        String firstName = "Auto" + suffix;
        String updatedFirstName = "AutoEdit" + suffix;
        String email = "auto." + suffix + "@gmail.com";
        String phone = "08" + suffix.substring(Math.max(0, suffix.length() - 10));

        assertVisibleText("Manajemen Pengguna");
        String userId = createAdminByApi(firstName, email, phone);
        readAdminByApi(userId, email);
        updateAdminByApi(userId, updatedFirstName, email, phone);
        readAdminByApi(userId, updatedFirstName);
        deleteAdminByApi(userId);
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
    }

    private void search(String query) {
        WebElement search = waitForPage().until(webDriver -> webDriver.findElements(By.cssSelector("input[placeholder*='Cari']")).stream()
                .filter(WebElement::isDisplayed)
                .filter(WebElement::isEnabled)
                .findFirst()
                .orElse(null));
        search.click();
        search.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        search.sendKeys(Keys.BACK_SPACE);
        search.sendKeys(query);
        WaitHelper.pauseForVisual();
    }

    private void openDetail(String email) {
        clickVisibleTextInScope(email, "Detail");
        assertVisibleText("Informasi Akun");
    }

    private void updateFirstName(String firstName) {
        typeByLabel("Nama Depan", firstName);
        clickVisibleTextLinkOrButton("Edit Informasi Akun");
        waitForAnySuccessText("berhasil", firstName);
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

    @SuppressWarnings("unchecked")
    private String createAdminByApi(String firstName, String email, String phone) {
        Map<String, Object> result = (Map<String, Object>) executeApiScript(
                "const firstName = a[0]; const email = a[1]; const phone = a[2];" +
                        "return await req('/admin/pengaturan-akun/admins', 'POST', {first_name: firstName, last_name: 'Automation', email, no_telp: phone, address: 'Jl. Malioboro No. 1, Yogyakarta', password: 'AdminAuto@2026!', confirm_password: 'AdminAuto@2026!'});",
                firstName, email, phone
        );
        Object id = firstNonNull(result.get("id"), result.get("user_id"), result.get("userId"));
        if (id == null) {
            throw new AssertionError("Create admin response has no id: " + result);
        }
        return String.valueOf(id);
    }

    @SuppressWarnings("unchecked")
    private void readAdminByApi(String userId, String expectedText) {
        Map<String, Object> result = (Map<String, Object>) executeApiScript("return await req('/admin/pengaturan-akun/admins/' + a[0], 'GET');", userId);
        String body = result.toString().toLowerCase();
        if (!body.contains(expectedText.toLowerCase())) {
            throw new AssertionError("Admin read response does not contain " + expectedText + ": " + result);
        }
    }

    private void updateAdminByApi(String userId, String firstName, String email, String phone) {
        executeApiScript(
                "return await req('/admin/pengaturan-akun/admins/' + a[0], 'PATCH', {first_name: a[1], last_name: 'Automation', email: a[2], no_telp: a[3], address: 'Jl. Malioboro No. 2, Yogyakarta'});",
                userId, firstName, email, phone
        );
    }

    private void deleteAdminByApi(String userId) {
        executeApiScript("return await req('/admin/pengaturan-akun/admins/' + a[0], 'DELETE');", userId);
    }

    private Object executeApiScript(String body, Object... args) {
        String script = "const done = arguments[arguments.length - 1];" +
                "const userArgs = Array.from(arguments).slice(0, -1);" +
                "const token = window.localStorage.getItem('access_token');" +
                "const base = '/sibudaya/api/v1';" +
                "const a = userArgs;" +
                "const req = async (path, method, payload) => {" +
                " const res = await fetch(base + path, {method, credentials: 'include', headers: {'Content-Type':'application/json', ...(token ? {Authorization:'Bearer ' + token} : {})}, ...(payload !== undefined ? {body: JSON.stringify(payload)} : {})});" +
                " const text = await res.text(); const data = text ? JSON.parse(text) : {};" +
                " if (!res.ok) throw new Error(method + ' ' + path + ' failed: ' + res.status + ' ' + text);" +
                " return data && data.data ? data.data : data;" +
                "};" +
                "(async () => {" + body + "})().then(done).catch((error) => done({__error: String(error && error.message ? error.message : error)}));";
        Object result = ((JavascriptExecutor) driver).executeAsyncScript(script, args);
        if (result instanceof Map<?, ?> map && map.containsKey("__error")) {
            throw new AssertionError(map.get("__error"));
        }
        return result;
    }

    private Object firstNonNull(Object... values) {
        for (Object value : values) {
            if (value != null) return value;
        }
        return null;
    }
}
