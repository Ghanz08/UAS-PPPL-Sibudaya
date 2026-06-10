package sibudaya.e2e.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import sibudaya.e2e.pages.AdminDashboardPage;
import sibudaya.e2e.pages.AdminDataPage;
import sibudaya.e2e.pages.AjukanFasilitasiPage;
import sibudaya.e2e.pages.AuthPage;
import sibudaya.e2e.pages.PengajuanFormPage;
import sibudaya.e2e.pages.StatusPengajuanPage;
import sibudaya.e2e.pages.UserDashboardPage;
import sibudaya.e2e.support.E2eContext;

public class SibudayaE2eSteps {
    private final E2eContext context;

    public SibudayaE2eSteps(E2eContext context) {
        this.context = context;
    }

    @Given("Sibudaya E2E credentials are configured")
    public void sibudayaE2eCredentialsAreConfigured() {
        new AuthPage(context.getDriver()).assertCredentialsConfigured();
    }

    @When("the ordinary user logs in to Sibudaya")
    public void theOrdinaryUserLogsInToSibudaya() {
        new AuthPage(context.getDriver()).loginAsUser();
    }

    @Then("the user dashboard page is shown")
    public void theUserDashboardPageIsShown() {
        new UserDashboardPage(context.getDriver()).assertShown();
    }

    @When("the user opens the facilitation selection page")
    public void theUserOpensTheFacilitationSelectionPage() {
        new UserDashboardPage(context.getDriver()).openFacilitationSelection();
    }

    @Then("the facilitation selection page is shown without submitting data")
    public void theFacilitationSelectionPageIsShownWithoutSubmittingData() {
        new AjukanFasilitasiPage(context.getDriver()).assertShownReadOnly();
    }

    @When("the user opens an existing submission status page if available")
    public void theUserOpensAnExistingSubmissionStatusPageIfAvailable() {
        new UserDashboardPage(context.getDriver()).openExistingStatusIfAvailable();
    }

    @Then("the user status page or dashboard fallback is shown")
    public void theUserStatusPageOrDashboardFallbackIsShown() {
        new StatusPengajuanPage(context.getDriver()).assertStatusOrDashboardShown();
    }

    @When("the user opens the profile page")
    public void theUserOpensTheProfilePage() {
        new UserDashboardPage(context.getDriver()).openProfile();
    }

    @Then("the user profile page is shown")
    public void theUserProfilePageIsShown() {
        new UserDashboardPage(context.getDriver()).assertProfileOrDashboardFallbackShown();
    }

    @When("the superadmin logs in to Sibudaya")
    public void theSuperadminLogsInToSibudaya() {
        new AuthPage(context.getDriver()).loginAsSuperadmin();
    }

    @Then("the superadmin dashboard page is shown")
    public void theSuperadminDashboardPageIsShown() {
        new AdminDashboardPage(context.getDriver()).assertShown();
    }

    @When("the superadmin opens a read-only administration page")
    public void theSuperadminOpensAReadOnlyAdministrationPage() {
        new AdminDataPage(context.getDriver()).openReadOnlyAdminPage();
    }

    @Then("the read-only administration page is shown")
    public void theReadOnlyAdministrationPageIsShown() {
        new AdminDataPage(context.getDriver()).assertShown();
    }

    @When("the user starts the first available facilitation submission")
    public void theUserStartsTheFirstAvailableFacilitationSubmission() {
        new AjukanFasilitasiPage(context.getDriver()).startFirstAvailableSubmission();
    }

    @When("the user completes and submits the facilitation form")
    public void theUserCompletesAndSubmitsTheFacilitationForm() {
        context.setSubmissionMarker(new PengajuanFormPage(context.getDriver()).completeAndSubmit());
    }

    @Then("the submitted request status page is shown")
    public void theSubmittedRequestStatusPageIsShown() {
        new StatusPengajuanPage(context.getDriver()).assertSubmittedStatusShown(context.getSubmissionMarker());
    }

    @When("the superadmin searches for the submitted request")
    public void theSuperadminSearchesForTheSubmittedRequest() {
        new AdminDashboardPage(context.getDriver()).openSubmittedRequest(context.getSubmissionMarker());
    }

    @Then("the submitted request is visible to the superadmin")
    public void theSubmittedRequestIsVisibleToTheSuperadmin() {
        new StatusPengajuanPage(context.getDriver()).assertVisibleAnyText(context.getSubmissionMarker(), "Pengajuan", "Status", "Fasilitasi");
    }
}
