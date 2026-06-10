# Sibudaya E2E Cucumber Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add Selenium+Cucumber+POM tests for Sibudaya production covering one real submit flow and one read-only flow, each visiting at least five pages.

**Architecture:** Add a new `sibudaya.e2e` test package beside the existing QA-4 package. Feature files define flows; step definitions hold no selectors; Page Objects own Selenium locators/actions; `E2eContext` carries generated submit marker across user and superadmin phases.

**Tech Stack:** Java 21, Maven, Selenium WebDriver 4.21, Cucumber 7.18, JUnit Platform Suite, WebDriverManager, ChromeDriver.

---

## File Map

- Create `src/test/resources/features/sibudaya_e2e_readonly.feature`: read-only 5+ page flow.
- Create `src/test/resources/features/sibudaya_e2e_submission.feature`: mutating submit + superadmin validation flow.
- Create `src/test/java/sibudaya/e2e/SibudayaE2eCucumberTest.java`: Cucumber runner.
- Create `src/test/java/sibudaya/e2e/support/E2eContext.java`: WebDriver + generated marker state.
- Create `src/test/java/sibudaya/e2e/support/E2eHooks.java`: browser open/close.
- Create `src/test/java/sibudaya/e2e/support/E2eTestData.java`: marker/date/PDF fixture helpers.
- Create `src/test/java/sibudaya/e2e/pages/*.java`: POMs for auth, user dashboard, facilitation list, form, status, admin dashboard, admin data page.
- Create `src/test/java/sibudaya/e2e/steps/SibudayaE2eSteps.java`: Cucumber glue.
- Create `src/test/resources/sibudaya/e2e/proposal-e2e-sample.pdf`: upload fixture copied from existing QA-4 fixture.
- Modify `src/test/resources/shared/config.properties`: add `sibudaya.e2e.*` credential keys.
- Modify `README.md`: add commands and production submit warning.

## Task 1: Feature Files and Runner

**Files:**
- Create: `D:\UAS-PPPL-Sibudaya\src\test\resources\features\sibudaya_e2e_readonly.feature`
- Create: `D:\UAS-PPPL-Sibudaya\src\test\resources\features\sibudaya_e2e_submission.feature`
- Create: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\SibudayaE2eCucumberTest.java`

- [ ] **Step 1: Write failing feature files**

`sibudaya_e2e_readonly.feature` content:

```gherkin
@sibudaya @e2e @readonly
Feature: Sibudaya read-only end-to-end navigation
  Background:
    Given Sibudaya E2E credentials are configured

  Scenario: Ordinary user and superadmin can open read-only pages in order
    When the ordinary user logs in to Sibudaya
    Then the user dashboard page is shown
    When the user opens the facilitation selection page
    Then the facilitation selection page is shown without submitting data
    When the user opens an existing submission status page if available
    Then the user status page or dashboard fallback is shown
    When the user opens the profile page
    Then the user profile page is shown
    When the superadmin logs in to Sibudaya
    Then the superadmin dashboard page is shown
    When the superadmin opens a read-only administration page
    Then the read-only administration page is shown
```

`sibudaya_e2e_submission.feature` content:

```gherkin
@sibudaya @e2e @submit
Feature: Sibudaya real submission end-to-end flow
  Background:
    Given Sibudaya E2E credentials are configured

  Scenario: Ordinary user submits a facilitation request and superadmin can see it
    When the ordinary user logs in to Sibudaya
    Then the user dashboard page is shown
    When the user opens the facilitation selection page
    And the user starts the first available facilitation submission
    And the user completes and submits the facilitation form
    Then the submitted request status page is shown
    When the superadmin logs in to Sibudaya
    Then the superadmin dashboard page is shown
    When the superadmin searches for the submitted request
    Then the submitted request is visible to the superadmin
```

- [ ] **Step 2: Add runner**

`SibudayaE2eCucumberTest.java` content:

```java
package sibudaya.e2e;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "sibudaya.e2e")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, summary")
public class SibudayaE2eCucumberTest {
}
```

- [ ] **Step 3: Verify expected failure**

Run: `mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @readonly"`

Expected: FAIL with undefined Cucumber steps, no Java compile error.

- [ ] **Step 4: Commit**

Run: `git add src/test/resources/features/sibudaya_e2e_readonly.feature src/test/resources/features/sibudaya_e2e_submission.feature src/test/java/sibudaya/e2e/SibudayaE2eCucumberTest.java && git commit -m "test: add sibudaya e2e cucumber scenarios"`

## Task 2: Support and Config

**Files:**
- Create: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\support\E2eContext.java`
- Create: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\support\E2eHooks.java`
- Create: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\support\E2eTestData.java`
- Modify: `D:\UAS-PPPL-Sibudaya\src\test\resources\shared\config.properties`

- [ ] **Step 1: Add config keys**

Append:

```properties

# Sibudaya E2E assignment accounts. Override with -Dkey=value or ENV KEY_WITH_UNDERSCORES.
sibudaya.e2e.user.identifier=AA/10-ABC-VENUS-YK.11
sibudaya.e2e.user.password=12345678
sibudaya.e2e.superadmin.identifier=superadmin@fasilitasi.go.id
sibudaya.e2e.superadmin.password=SuperAdmin@2026!
```

- [ ] **Step 2: Add context and hooks**

Create `E2eContext.java`:

```java
package sibudaya.e2e.support;

import org.openqa.selenium.WebDriver;

public class E2eContext {
    private WebDriver driver;
    private String submissionMarker;
    public WebDriver getDriver() { return driver; }
    public void setDriver(WebDriver driver) { this.driver = driver; }
    public String getSubmissionMarker() { return submissionMarker; }
    public void setSubmissionMarker(String submissionMarker) { this.submissionMarker = submissionMarker; }
}
```

Create `E2eHooks.java`:

```java
package sibudaya.e2e.support;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import shared.core.DriverFactory;

public class E2eHooks {
    private final E2eContext context;
    public E2eHooks(E2eContext context) { this.context = context; }
    @Before("@sibudaya") public void openBrowser() { context.setDriver(DriverFactory.createChromeDriver()); }
    @After("@sibudaya") public void closeBrowser() { if (context.getDriver() != null) context.getDriver().quit(); }
}
```

Create `E2eTestData.java`:

```java
package sibudaya.e2e.support;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class E2eTestData {
    private E2eTestData() {}
    public static String marker() { return "AUTO-E2E-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()); }
    public static LocalDate eventDate() { return LocalDate.now().plusDays(30); }
    public static Path proposalPdfPath() {
        try {
            return Paths.get(Objects.requireNonNull(E2eTestData.class.getClassLoader().getResource("sibudaya/e2e/proposal-e2e-sample.pdf"), "Missing proposal-e2e-sample.pdf").toURI()).toAbsolutePath();
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Invalid proposal fixture path", exception);
        }
    }
}
```

- [ ] **Step 3: Verify compile**

Run: `mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @readonly"`

Expected: FAIL with undefined Cucumber steps, no Java compile error.

- [ ] **Step 4: Commit**

Run: `git add src/test/java/sibudaya/e2e/support src/test/resources/shared/config.properties && git commit -m "test: add sibudaya e2e support context"`

## Task 3: Base/Auth POM and Read-Only Flow

**Files:**
- Create: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\pages\BaseE2ePage.java`
- Create: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\pages\AuthPage.java`
- Create: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\pages\UserDashboardPage.java`
- Create: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\pages\AjukanFasilitasiPage.java`
- Create: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\pages\StatusPengajuanPage.java`
- Create: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\pages\AdminDashboardPage.java`
- Create: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\pages\AdminDataPage.java`
- Create: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\steps\SibudayaE2eSteps.java`

- [ ] **Step 1: Implement POM helpers**

Implement `BaseE2ePage` with these methods copied/adapted from existing QA-4 `BasePage`: `openPath`, `waitForUrlContains`, `assertVisibleAnyText`, `clickTextLinkOrButton`, `typeByNameIfPresent`, `setDateByNameIfPresent`, `uploadPdf`, and `xpathLiteral`. Use `ConfigLoader.getBaseUrl()` and `WaitHelper.defaultWait(driver)` exactly as QA-4 does.

Implement `AuthPage` with config keys `sibudaya.e2e.user.identifier`, `sibudaya.e2e.user.password`, `sibudaya.e2e.superadmin.identifier`, `sibudaya.e2e.superadmin.password`. Login must clear cookies/localStorage, open `/login`, fill identifier via `input[placeholder='NIK Lembaga atau Email Admin']` fallback to `input[type='text']`, fill `input[type='password']`, click `Masuk`, wait for `access_token` and `refresh_token`, then copy both into secure cookies like existing QA-4 `AuthPage`.

- [ ] **Step 2: Implement read-only page objects**

Implement methods:

```java
// UserDashboardPage
assertShown(); openFacilitationSelection(); openExistingStatusIfAvailable(); openProfile(); assertProfileOrDashboardFallbackShown();

// AjukanFasilitasiPage
assertShownReadOnly(); startFirstAvailableSubmission();

// StatusPengajuanPage
assertStatusOrDashboardShown(); assertSubmittedStatusShown(String marker);

// AdminDashboardPage
assertShown(); openSubmittedRequest(String marker);

// AdminDataPage
openReadOnlyAdminPage(); assertShown();
```

Selector contract: use route fragments first (`/dashboard`, `/dashboard/ajukan-fasilitasi`, `/dashboard/status/`, `/dashboard/admin`, `/dashboard/admin/status/`), then visible domain text (`Dashboard`, `Fasilitasi`, `Pengajuan`, `Lembaga`, `Manajemen`). Do not click create/update/delete/review buttons in read-only methods.

- [ ] **Step 3: Implement read-only step definitions**

Create `SibudayaE2eSteps` constructor accepting `E2eContext`, instantiate all page objects from `context.getDriver()`, and bind every read-only Gherkin step to one POM call. No `By.*` selector is allowed in this step file.

- [ ] **Step 4: Run read-only test**

Run: `mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @readonly"`

Expected: PASS, or a selector/page assertion failure with current URL. No undefined steps.

- [ ] **Step 5: Commit read-only implementation**

Run: `git add src/test/java/sibudaya/e2e/pages src/test/java/sibudaya/e2e/steps/SibudayaE2eSteps.java && git commit -m "test: add sibudaya e2e readonly flow"`

## Task 4: Submit Flow

**Files:**
- Create: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\pages\PengajuanFormPage.java`
- Modify: `D:\UAS-PPPL-Sibudaya\src\test\java\sibudaya\e2e\steps\SibudayaE2eSteps.java`
- Create: `D:\UAS-PPPL-Sibudaya\src\test\resources\sibudaya\e2e\proposal-e2e-sample.pdf`

- [ ] **Step 1: Add PDF fixture**

Run:

```powershell
New-Item -ItemType Directory -Force "src/test/resources/sibudaya/e2e" | Out-Null
Copy-Item "src/test/resources/qa4/proposal-revisi-sample.pdf" "src/test/resources/sibudaya/e2e/proposal-e2e-sample.pdf"
```

Expected: `src/test/resources/sibudaya/e2e/proposal-e2e-sample.pdf` exists.

- [ ] **Step 2: Implement form POM**

Create `PengajuanFormPage` with `completeAndSubmit()` returning a generated marker. Required behavior:

```java
String marker = E2eTestData.marker();
typeByNameIfPresent("namaLembaga", "Automation Sibudaya " + marker);
typeByNameIfPresent("judulKegiatan", "Pengujian Otomatis " + marker);
typeByNameIfPresent("namaKegiatan", "Pengujian Otomatis " + marker);
typeByNameIfPresent("deskripsi", "Data dibuat oleh automation end-to-end " + marker);
typeByNameIfPresent("tujuan", "Pengujian end-to-end Sibudaya " + marker);
typeByNameIfPresent("alamat", "Jl. Malioboro No. 1, Yogyakarta");
typeByNameIfPresent("alamatLembaga", "Jl. Malioboro No. 1, Yogyakarta");
typeByNameIfPresent("email", "automation@example.test");
typeByNameIfPresent("noHp", "081234567890");
typeByNameIfPresent("no_hp", "081234567890");
setDateByNameIfPresent("tanggalKegiatan", E2eTestData.eventDate());
setDateByNameIfPresent("tanggalMulai", E2eTestData.eventDate());
setDateByNameIfPresent("tanggalSelesai", E2eTestData.eventDate().plusDays(1));
```

Then click `Selanjutnya` when present, upload `E2eTestData.proposalPdfPath()` when `input[type='file']` exists, click one of `Ajukan`, `Kirim`, or `Submit`, and wait for `/dashboard/status/` or visible `Berhasil`/`Pengajuan`.

- [ ] **Step 3: Add submit step definitions**

Extend `SibudayaE2eSteps` with:

```java
@When("the user starts the first available facilitation submission")
public void startSubmission() { ajukanFasilitasiPage.startFirstAvailableSubmission(); }

@When("the user completes and submits the facilitation form")
public void submitForm() { context.setSubmissionMarker(pengajuanFormPage.completeAndSubmit()); }

@Then("the submitted request status page is shown")
public void submittedStatusShown() { statusPengajuanPage.assertSubmittedStatusShown(context.getSubmissionMarker()); }

@When("the superadmin searches for the submitted request")
public void adminSearchesSubmittedRequest() { adminDashboardPage.openSubmittedRequest(context.getSubmissionMarker()); }

@Then("the submitted request is visible to the superadmin")
public void submittedRequestVisibleToAdmin() { statusPengajuanPage.assertVisibleAnyText(context.getSubmissionMarker(), "Pengajuan", "Status", "Fasilitasi"); }
```

- [ ] **Step 4: Run submit test**

Run: `mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @submit"`

Expected: PASS if the account has an available facilitation type. If unavailable, abort with `No available facilitation submission link is visible for this account.` No undefined steps.

- [ ] **Step 5: Commit submit implementation**

Run: `git add src/test/java/sibudaya/e2e/pages/PengajuanFormPage.java src/test/java/sibudaya/e2e/steps/SibudayaE2eSteps.java src/test/resources/sibudaya/e2e/proposal-e2e-sample.pdf && git commit -m "test: add sibudaya e2e submit flow"`

## Task 5: Docs and Final Verification

**Files:**
- Modify: `D:\UAS-PPPL-Sibudaya\README.md`

- [ ] **Step 1: Add README commands**

Append:

```markdown

## Sibudaya E2E Cucumber

Target URL: `https://www.sibudaya.cloud/sibudaya`

Read-only flow:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @readonly"
```

Submit flow, creates real production data:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @submit"
```

Both flows:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @e2e"
```

Warning: `@submit` creates a real facilitation submission in production. Use `@readonly` when production data must not change.
```

- [ ] **Step 2: Run full suite**

Run: `mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @e2e"`

Expected: PASS for both flows, or read-only PASS plus submit assumption abort only when no facilitation type is available. No undefined steps and no compile failures.

- [ ] **Step 3: Inspect diff**

Run: `git status --short && git diff --stat`

Expected: only E2E files, README, config, and fixture are changed; pre-existing unrelated dirty files are not reverted.

- [ ] **Step 4: Commit docs**

Run: `git add README.md && git commit -m "docs: add sibudaya e2e test commands"`

## Self-Review

- Spec coverage: submit feature, read-only feature, POM separation, generated marker, PDF fixture, credential config, tag commands, README warning, and production mutation distinction are covered.
- Placeholder scan: no unresolved placeholder token or design choice remains.
- Type consistency: package root is `sibudaya.e2e`; context is `E2eContext`; test data helper is `E2eTestData`; runner is `SibudayaE2eCucumberTest`.
