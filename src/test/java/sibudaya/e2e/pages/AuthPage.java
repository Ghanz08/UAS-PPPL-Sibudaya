package sibudaya.e2e.pages;

import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import shared.core.ConfigLoader;
import shared.utils.WaitHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthPage extends BaseE2ePage {
    private static final String USER_IDENTIFIER = "sibudaya.e2e.user.identifier";
    private static final String USER_PASSWORD = "sibudaya.e2e.user.password";
    private static final String SUPERADMIN_IDENTIFIER = "sibudaya.e2e.superadmin.identifier";
    private static final String SUPERADMIN_PASSWORD = "sibudaya.e2e.superadmin.password";
    private static final int LOGIN_MAX_ATTEMPTS = 8;
    private static final long LOGIN_RETRY_DELAY_MS = 10_000L;
    private static final Map<String, TokenPair> TOKEN_CACHE = new ConcurrentHashMap<>();

    public AuthPage(WebDriver driver) {
        super(driver);
    }

    public void assertCredentialsConfigured() {
        requiredConfig(USER_IDENTIFIER);
        requiredConfig(USER_PASSWORD);
        requiredConfig(SUPERADMIN_IDENTIFIER);
        requiredConfig(SUPERADMIN_PASSWORD);
    }

    public void loginAsUser() {
        loginAndOpenWithRetry(requiredConfig(USER_IDENTIFIER), requiredConfig(USER_PASSWORD), "/dashboard",
                "Dashboard", "Pantau perkembangan", "Ajukan Fasilitasi", "Riwayat Fasilitasi");
    }

    public void loginAsSuperadmin() {
        loginAndOpenWithRetry(requiredConfig(SUPERADMIN_IDENTIFIER), requiredConfig(SUPERADMIN_PASSWORD), "/dashboard/admin",
                "Dashboard", "Permohonan", "Lembaga", "Fasilitasi", "Manajemen");
    }

    private void loginAndOpenWithRetry(String identifier, String password, String path, String... dashboardTexts) {
        AssertionError lastAssertion = null;
        RuntimeException lastRuntime = null;
        String cacheKey = identifier + "\n" + password;
        for (int attempt = 1; attempt <= LOGIN_MAX_ATTEMPTS; attempt++) {
            try {
                clearSession();
                TokenPair cached = TOKEN_CACHE.get(cacheKey);
                if (cached != null) {
                    openPath("/login");
                    applyTokens(cached.accessToken(), cached.refreshToken());
                } else {
                    loginViaApi(identifier, password, cacheKey);
                }
                openPath(path);
                waitForDashboard(path, dashboardTexts);
                return;
            } catch (AssertionError failure) {
                TOKEN_CACHE.remove(cacheKey);
                lastAssertion = failure;
            } catch (RuntimeException failure) {
                lastRuntime = failure;
            }
            if (attempt < LOGIN_MAX_ATTEMPTS) {
                pauseBeforeLoginRetry();
            }
        }
        if (lastAssertion != null) {
            if (lastRuntime != null) {
                lastAssertion.addSuppressed(lastRuntime);
            }
            throw lastAssertion;
        }
        throw lastRuntime == null ? new IllegalStateException("Login failed without captured cause") : lastRuntime;
    }

    private void waitForDashboard(String path, String... dashboardTexts) {
        waitForUrlAndAnyText(path, dashboardTexts);
        try {
            Thread.sleep(1_500L);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while checking dashboard session", exception);
        }
        waitForUrlAndAnyText(path, dashboardTexts);
    }

    private void pauseBeforeLoginRetry() {
        try {
            Thread.sleep(LOGIN_RETRY_DELAY_MS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting before login retry", exception);
        }
    }

    @SuppressWarnings("unchecked")
    private void loginViaApi(String identifier, String password, String cacheKey) {
        openPath("/login");
        Map<String, Object> result = (Map<String, Object>) ((JavascriptExecutor) driver).executeAsyncScript(
                "const done = arguments[arguments.length - 1];" +
                        "const identifier = arguments[0]; const password = arguments[1];" +
                        "const payload = identifier.includes('@') ? {email: identifier, password} : {nik_lembaga: identifier, password};" +
                        "fetch('/sibudaya/api/v1/auth/login', {method: 'POST', credentials: 'include', headers: {'Content-Type':'application/json'}, body: JSON.stringify(payload)})" +
                        ".then(async (res) => { const text = await res.text(); const data = text ? JSON.parse(text) : {}; return {ok: res.ok, status: res.status, text, data}; })" +
                        ".then((result) => { if (!result.ok) { done({ok:false, status: result.status, message: result.text}); return; } const data = result.data && result.data.data ? result.data.data : result.data; const access = data.access_token || data.accessToken; const refresh = data.refresh_token || data.refreshToken; if (!access || !refresh) { done({ok:false, status: 0, message:'Login response missing tokens'}); return; } window.localStorage.setItem('access_token', access); window.localStorage.setItem('refresh_token', refresh); done({ok:true}); })" +
                        ".catch((error) => done({ok:false, status:0, message: String(error && error.message ? error.message : error)}));",
                identifier,
                password
        );
        if (!Boolean.TRUE.equals(result.get("ok"))) {
            String message = String.valueOf(result.get("message"));
            Object status = result.get("status");
            throw new IllegalStateException("Login failed: " + status + " " + message);
        }
        String accessToken = (String) ((JavascriptExecutor) driver).executeScript("return window.localStorage.getItem('access_token');");
        String refreshToken = (String) ((JavascriptExecutor) driver).executeScript("return window.localStorage.getItem('refresh_token');");
        TOKEN_CACHE.put(cacheKey, new TokenPair(accessToken, refreshToken));
        copyTokensToCookies();
    }

    private void login(String identifier, String password) {
        clearSession();
        openPath("/login");
        WebElement identifierInput = waitForPage().until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='NIK Lembaga atau Email Admin'], input[name='identifier'], input[type='text'], input[type='email']")
        ));
        identifierInput.clear();
        identifierInput.sendKeys(identifier);

        WebElement passwordInput = driver.findElement(By.cssSelector("input[type='password']"));
        passwordInput.clear();
        passwordInput.sendKeys(password);
        WaitHelper.pauseForVisual();
        driver.findElement(By.xpath("//button[contains(normalize-space(.), 'Masuk')]")).click();

        waitForPage().until(webDriver -> Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                "return Boolean(window.localStorage.getItem('access_token') && window.localStorage.getItem('refresh_token'));"
        )));
        copyTokensToCookies();
    }

    private void copyTokensToCookies() {
        String accessToken = (String) ((JavascriptExecutor) driver).executeScript("return window.localStorage.getItem('access_token');");
        String refreshToken = (String) ((JavascriptExecutor) driver).executeScript("return window.localStorage.getItem('refresh_token');");
        applyTokens(accessToken, refreshToken);
    }

    private void applyTokens(String accessToken, String refreshToken) {
        driver.get(ConfigLoader.getBaseUrl().replaceAll("/+$", "/"));
        boolean secureCookie = ConfigLoader.getBaseUrl().toLowerCase().startsWith("https://");
        driver.manage().addCookie(new Cookie.Builder("access_token", accessToken).path("/").isHttpOnly(true).isSecure(secureCookie).build());
        driver.manage().addCookie(new Cookie.Builder("refresh_token", refreshToken).path("/").isHttpOnly(true).isSecure(secureCookie).build());
        ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.setItem('access_token', arguments[0]); window.localStorage.setItem('refresh_token', arguments[1]);",
                accessToken,
                refreshToken
        );
    }

    @SuppressWarnings("unchecked")
    private void validateAppliedTokens(String cacheKey) {
        Map<String, Object> result = (Map<String, Object>) ((JavascriptExecutor) driver).executeAsyncScript(
                "const done = arguments[arguments.length - 1];" +
                        "const token = window.localStorage.getItem('access_token');" +
                        "fetch('/sibudaya/api/v1/auth/me', {method: 'GET', credentials: 'include', headers: token ? {Authorization: 'Bearer ' + token} : {}})" +
                        ".then(async (res) => { const text = await res.text(); done({ok: res.ok, status: res.status, text}); })" +
                        ".catch((error) => done({ok:false, status:0, text:String(error && error.message ? error.message : error)}));"
        );
        if (!Boolean.TRUE.equals(result.get("ok"))) {
            TOKEN_CACHE.remove(cacheKey);
            clearSession();
            throw new IllegalStateException("Cached login token invalid: " + result.get("status") + " " + result.get("text"));
        }
    }

    private record TokenPair(String accessToken, String refreshToken) {}

    private void clearSession() {
        openPath("/");
        driver.manage().deleteAllCookies();
        ((JavascriptExecutor) driver).executeAsyncScript(
                "const done = arguments[arguments.length - 1];" +
                        "try { window.localStorage.clear(); window.sessionStorage.clear(); } catch (e) {}" +
                        "const cacheClear = window.caches ? window.caches.keys().then(keys => Promise.all(keys.map((key) => window.caches.delete(key)))) : Promise.resolve();" +
                        "const dbClear = window.indexedDB && window.indexedDB.databases ? window.indexedDB.databases().then((dbs) => Promise.all(dbs.map((db) => db && db.name ? new Promise((resolve) => { const req = window.indexedDB.deleteDatabase(db.name); req.onsuccess = req.onerror = req.onblocked = resolve; }) : Promise.resolve()))) : Promise.resolve();" +
                        "Promise.allSettled([cacheClear, dbClear]).then(() => done(true)).catch(() => done(true));"
        );
        driver.manage().deleteAllCookies();
    }

    private String requiredConfig(String key) {
        String value = ConfigLoader.getOptional(key);
        Assumptions.assumeTrue(value != null, "Missing required config: " + key);
        return value;
    }
}
