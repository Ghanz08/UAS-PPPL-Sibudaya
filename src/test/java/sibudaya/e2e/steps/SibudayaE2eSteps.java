package sibudaya.e2e.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import sibudaya.e2e.pages.AdminDashboardPage;
import sibudaya.e2e.pages.AdminFasilitasiSettingsPage;
import sibudaya.e2e.pages.AdminUserManagementPage;
import sibudaya.e2e.pages.AjukanFasilitasiPage;
import sibudaya.e2e.pages.AuthPage;
import sibudaya.e2e.pages.PengajuanFormPage;
import sibudaya.e2e.pages.StatusPengajuanPage;
import sibudaya.e2e.pages.UserDashboardPage;
import sibudaya.e2e.pages.UserProfilePage;
import sibudaya.e2e.support.AuthHelper;
import sibudaya.e2e.support.E2eContext;
import sibudaya.e2e.support.FasilitasiType;

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
        AuthHelper.loginAsUser(context.getDriver());
    }

    @Then("the user dashboard page is shown")
    public void theUserDashboardPageIsShown() {
        new UserDashboardPage(context.getDriver()).assertShown();
    }

    @When("the user opens the facilitation selection page")
    public void theUserOpensTheFacilitationSelectionPage() {
        new UserDashboardPage(context.getDriver()).openFacilitationSelection();
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
        AuthHelper.loginAsSuperadmin(context.getDriver());
    }

    @Then("the superadmin dashboard page is shown")
    public void theSuperadminDashboardPageIsShown() {
        new AdminDashboardPage(context.getDriver()).assertShown();
    }

    @When("the user starts a {word} facilitation submission")
    public void theUserStartsAFacilitationSubmission(String type) {
        new AjukanFasilitasiPage(context.getDriver()).startSubmission(FasilitasiType.fromLabel(type));
    }

    @When("the user completes and submits the {word} facilitation form")
    public void theUserCompletesAndSubmitsTheTypedFacilitationForm(String type) {
        context.setSubmissionMarker(new PengajuanFormPage(context.getDriver()).completeAndSubmit(FasilitasiType.fromLabel(type)));
    }

    @When("the superadmin performs CRUD on the {word} facilitation settings page")
    public void theSuperadminPerformsCrudOnTheFacilitationSettingsPage(String type) {
        AdminFasilitasiSettingsPage page = new AdminFasilitasiSettingsPage(context.getDriver());
        page.open();
        page.performCrud(FasilitasiType.fromLabel(type));
    }

    @When("the superadmin performs CRUD on the user management page")
    public void theSuperadminPerformsCrudOnTheUserManagementPage() {
        AdminUserManagementPage page = new AdminUserManagementPage(context.getDriver());
        page.open();
        page.performCrud();
    }

    @When("the user updates the Data Kepala Lembaga first name")
    public void theUserUpdatesTheDataKepalaLembagaFirstName() {
        UserProfilePage page = new UserProfilePage(context.getDriver());
        page.open();
        page.updateKepalaLembagaFirstName();
    }

    @Then("the submitted request status page is shown")
    public void theSubmittedRequestStatusPageIsShown() {
        new StatusPengajuanPage(context.getDriver()).assertSubmittedStatusShown(context.getSubmissionMarker());
    }
}
