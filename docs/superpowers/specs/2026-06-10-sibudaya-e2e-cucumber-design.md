# Sibudaya End-to-End Cucumber Test Design

## Context

The project at `D:\UAS-PPPL-Sibudaya` is a Maven Java QA automation workspace using Selenium WebDriver, JUnit 5, Cucumber/Gherkin, WebDriverManager, and Page Object Model. The target application is the production Sibudaya frontend at `https://www.sibudaya.cloud/sibudaya`.

The existing repository already contains a QA-4 Cucumber/POM test area under `src/test/java/qa4/reviewconfig` and shared browser/config helpers under `src/test/java/shared`. The new work will follow the same stack and style, while adding a broader end-to-end suite that covers at least five pages in order.

## Goal

Add Cucumber-based Selenium end-to-end tests for Sibudaya that use Page Object Model and Gherkin. The suite must include both:

1. A submit flow that creates a real facilitation submission using the ordinary user account, then validates the submitted request from the superadmin side.
2. A read-only flow that visits and validates at least five pages without changing production data.

## Accounts and Configuration

Tests will read credentials from system properties, environment variables, or config properties. The implementation may provide defaults matching the requested assignment accounts, but credentials should stay centralized in config rather than duplicated inside step definitions.

Required logical credentials:

- Ordinary user identifier: `AA/10-ABC-VENUS-YK.11`
- Ordinary user password: `12345678`
- Superadmin identifier: `superadmin@fasilitasi.go.id`
- Superadmin password: `SuperAdmin@2026!`

The base URL remains `https://www.sibudaya.cloud/sibudaya`, with path helpers responsible for joining route paths safely.

## Test Features

### Submit End-to-End Feature

Feature file: `src/test/resources/features/sibudaya_e2e_submission.feature`

Tags: `@sibudaya @e2e @submit`

Primary scenario:

1. Open login page.
2. Log in as the ordinary user.
3. Validate the user dashboard is shown.
4. Open the facilitation submission page.
5. Choose an available facilitation type.
6. Fill required submission form fields using assignment-safe generated values.
7. Upload a PDF fixture from test resources.
8. Submit the request.
9. Validate the user status/detail page shows the submitted request.
10. Log out or reset session.
11. Log in as superadmin.
12. Open superadmin dashboard.
13. Locate the newly submitted request by generated title or institution context.
14. Open the admin request detail page.
15. Validate the request detail/status is visible.

This scenario intentionally mutates production data because the user approved real submission.

### Read-Only End-to-End Feature

Feature file: `src/test/resources/features/sibudaya_e2e_readonly.feature`

Tags: `@sibudaya @e2e @readonly`

Primary scenario:

1. Open login page.
2. Log in as the ordinary user.
3. Validate user dashboard.
4. Open facilitation submission selection page and validate options are visible without submitting.
5. Open an existing status/history page when an existing request link is available.
6. Open user profile or account page and validate profile content.
7. Reset session and log in as superadmin.
8. Validate superadmin dashboard.
9. Open an admin data page such as lembaga budaya, pengaturan fasilitasi, or manajemen pengguna.
10. Validate the page loads without creating, updating, deleting, approving, or rejecting data.

The read-only suite should use assertions based on visible headings, route fragments, table/list presence, and stable controls. It must avoid buttons that change state, submit forms, upload files, or alter review status.

## Page Object Model

New or extended POM classes will keep Selenium selectors out of step definitions. Each page object should expose business actions and assertions, not raw locator details.

Planned page objects:

- `AuthPage`: login, logout/session reset, role-specific login helpers.
- `UserDashboardPage`: assert dashboard, open submission flow, open status/history links.
- `AjukanFasilitasiPage`: assert facilitation choices, choose first available safe option.
- `PengajuanFormPage`: fill required steps, upload PDF, submit, return generated request marker.
- `StatusPengajuanPage`: assert submitted request/status detail is visible.
- `AdminDashboardPage`: assert admin dashboard, search/open latest matching request.
- `AdminPengajuanPage`: assert admin detail for submitted request.
- Optional `AdminDataPage`: read-only assertions for superadmin pages when needed.

Selectors should prefer stable attributes already present in the frontend, in this order:

1. `name`, `id`, `role`, `aria-label`, and form control attributes.
2. Route fragments and links containing stable path parts.
3. Visible text only when it is domain copy that is unlikely to change.
4. XPath as a fallback for complex text/button cases.

## Test Data

The submit scenario will generate unique values using timestamp-based markers, for example a title or contact note containing `AUTO-E2E-<timestamp>`. This marker lets the admin side find the request that was just created.

Required fixture:

- A small valid PDF under `src/test/resources/sibudaya/e2e/`, used for proposal upload.

If the ordinary user account already has an active submission that blocks a facilitation type, the implementation should choose another available option when possible. If no safe option is available, the submit scenario should fail with a clear message explaining the data precondition.

## Error Handling and Diagnostics

Failures should include enough context for assignment debugging:

- Current URL in assertion messages where useful.
- Clear failure when credentials are missing or login fails.
- Clear failure when no submit option is available.
- Explicit distinction between submit and read-only tests.

The tests should wait for user-visible page state with Selenium explicit waits. Avoid fixed sleeps except as a last resort for brief UI transitions.

## Runner and Commands

Add a Cucumber runner for the new suite, for example `SibudayaE2eCucumberTest`.

Expected commands:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @readonly"
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @submit"
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @e2e"
```

Documentation should explain that `@submit` changes production data, while `@readonly` does not.

## Out of Scope

- No backend database seeding.
- No direct API calls for setup unless needed only for diagnostics.
- No approval, rejection, or destructive admin status changes in the read-only flow.
- No redesign of the existing frontend or automation framework.

## Acceptance Criteria

- The project contains Gherkin feature files for submit and read-only flows.
- Step definitions use POM classes instead of raw Selenium selectors in steps.
- Each flow visits at least five distinct Sibudaya pages in order.
- The submit flow logs in as ordinary user, submits a request, then logs in as superadmin and validates the request is visible.
- The read-only flow validates at least five pages without mutating production data.
- Maven can run each flow by Cucumber tags.
- README or equivalent docs include exact commands and a warning for the mutating submit flow.
