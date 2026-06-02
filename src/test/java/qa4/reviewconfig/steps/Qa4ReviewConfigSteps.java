package qa4.reviewconfig.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assumptions;
import qa4.reviewconfig.pages.AdminPengajuanPage;
import qa4.reviewconfig.pages.AuthPage;
import qa4.reviewconfig.pages.DashboardPage;
import qa4.reviewconfig.pages.PengajuanDetailPage;
import qa4.reviewconfig.pages.PengajuanFormPage;
import qa4.reviewconfig.support.Qa4Context;
import qa4.reviewconfig.support.TargetStatus;
import shared.core.ConfigLoader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Qa4ReviewConfigSteps {
    private static final String TIMELINE_PENGAJUAN_ID = "qa4.timeline.pengajuan.id";
    private static final String REVISI_PENGAJUAN_ID = "qa4.revisi.pengajuan.id";
    private static final String SELESAI_PENGAJUAN_ID = "qa4.selesai.pengajuan.id";
    private static final String DITOLAK_PENGAJUAN_ID = "qa4.ditolak.pengajuan.id";
    private static final String EMPTY_DASHBOARD_ENABLED = "qa4.empty.dashboard.enabled";
    private static final String AUTO_CREATE_TEST_DATA = "qa4.auto.create.test.data";
    private static final Map<String, String> RUNTIME_PENGAJUAN_IDS = new ConcurrentHashMap<>();

    private final Qa4Context context;

    public Qa4ReviewConfigSteps(Qa4Context context) {
        this.context = context;
    }

    @Given("QA4 account configuration is available")
    public void qa4AccountConfigurationIsAvailable() {
        new AuthPage(context.driver()).assertQa4AccountsConfigured();
    }

    @Given("a {string} pengajuan is available")
    public void aPengajuanIsAvailable(String statusName) {
        TargetStatus targetStatus = TargetStatus.valueOf(statusName);
        context.setTargetStatus(targetStatus);
        context.setPengajuanId(resolvePengajuanId(targetStatus));
    }

    @Given("empty dashboard validation is enabled")
    public void emptyDashboardValidationIsEnabled() {
        Assumptions.assumeTrue(
                ConfigLoader.getBoolean(EMPTY_DASHBOARD_ENABLED, false),
                "Set qa4.empty.dashboard.enabled=true only for empty database validation."
        );
    }

    @When("the lembaga opens the pengajuan detail")
    public void theLembagaOpensThePengajuanDetail() {
        new AuthPage(context.driver()).loginAsLembaga();
        new PengajuanDetailPage(context.driver()).openDetail(context.pengajuanId());
    }

    @When("the lembaga uploads a revised proposal")
    public void theLembagaUploadsARevisedProposal() {
        AuthPage authPage = new AuthPage(context.driver());
        PengajuanDetailPage detailPage = new PengajuanDetailPage(context.driver());
        PengajuanFormPage formPage = new PengajuanFormPage(context.driver());

        authPage.loginAsLembaga();
        detailPage.openDetail(context.pengajuanId());
        detailPage.assertVisibleText("Perlu Revisi");
        formPage.submitRevision(context.pengajuanId(), detailPage.isHibah());
    }

    @When("the admin opens the dashboard")
    public void theAdminOpensTheDashboard() {
        new AuthPage(context.driver()).loginAsAdmin();
        new DashboardPage(context.driver()).openAdminDashboard();
    }

    @Then("the chronological timeline status is shown")
    public void theChronologicalTimelineStatusIsShown() {
        new PengajuanDetailPage(context.driver()).assertTimelineChronological();
    }

    @Then("the revision submission is accepted")
    public void theRevisionSubmissionIsAccepted() {
        new PengajuanDetailPage(context.driver()).assertVisibleText("Revisi pengajuan berhasil dikirim.");
    }

    @And("the admin notification shows the updated pengajuan")
    public void theAdminNotificationShowsTheUpdatedPengajuan() {
        AuthPage authPage = new AuthPage(context.driver());
        AdminPengajuanPage adminPage = new AdminPengajuanPage(context.driver());

        authPage.loginAsAdmin();
        adminPage.openNotifications();
        adminPage.assertVisibleAnyText(
                "Pengajuan Pentas Diperbarui",
                "Pengajuan Hibah Diperbarui",
                "Pemohon telah memperbarui data pengajuan"
        );
    }

    @Then("the finished pengajuan has no revision upload action")
    public void theFinishedPengajuanHasNoRevisionUploadAction() {
        new PengajuanDetailPage(context.driver()).assertFinishedNotEditable();
    }

    @Then("the rejected pengajuan has no revision upload action")
    public void theRejectedPengajuanHasNoRevisionUploadAction() {
        new PengajuanDetailPage(context.driver()).assertRejectedNotEditable();
    }

    @Then("the admin dashboard summary is shown")
    public void theAdminDashboardSummaryIsShown() {
        new DashboardPage(context.driver()).assertAdminSummaryShown();
    }

    @Then("the empty admin dashboard is shown without backend error")
    public void theEmptyAdminDashboardIsShownWithoutBackendError() {
        new DashboardPage(context.driver()).assertEmptyDashboardShownWithoutBackendError();
    }

    private String resolvePengajuanId(TargetStatus targetStatus) {
        String configuredId = optionalPengajuanId(configKeyFor(targetStatus));
        if (configuredId != null) {
            return configuredId;
        }

        Assumptions.assumeTrue(
                ConfigLoader.getBoolean(AUTO_CREATE_TEST_DATA, false),
                "Missing " + configKeyFor(targetStatus) + "; set qa4.auto.create.test.data=true to create cloud data through UI."
        );

        return RUNTIME_PENGAJUAN_IDS.computeIfAbsent(targetStatus.name(), ignored -> createPengajuanForTarget(targetStatus));
    }

    private String optionalPengajuanId(String configKey) {
        String value = ConfigLoader.getOptional(configKey);
        if (value == null || value.startsWith("id-pengajuan")) {
            return null;
        }
        return value;
    }

    private String configKeyFor(TargetStatus targetStatus) {
        return switch (targetStatus) {
            case TIMELINE -> TIMELINE_PENGAJUAN_ID;
            case REVISI -> REVISI_PENGAJUAN_ID;
            case SELESAI -> SELESAI_PENGAJUAN_ID;
            case DITOLAK -> DITOLAK_PENGAJUAN_ID;
        };
    }

    private String createPengajuanForTarget(TargetStatus targetStatus) {
        AuthPage authPage = new AuthPage(context.driver());
        PengajuanFormPage formPage = new PengajuanFormPage(context.driver());
        AdminPengajuanPage adminPage = new AdminPengajuanPage(context.driver());
        PengajuanDetailPage detailPage = new PengajuanDetailPage(context.driver());

        authPage.loginAsLembaga();
        String pengajuanId = formPage.createPentasPengajuan(targetStatus);

        if (targetStatus == TargetStatus.TIMELINE) {
            return pengajuanId;
        }

        authPage.loginAsAdmin();
        adminPage.openAdminPengajuanDetail(pengajuanId);

        if (targetStatus == TargetStatus.REVISI) {
            adminPage.requestTimelineRevision("QA4 auto revisi proposal");
            adminPage.assertVisibleText("Perlu Revisi");
            return pengajuanId;
        }

        if (targetStatus == TargetStatus.DITOLAK) {
            adminPage.rejectTimeline("QA4 auto tolak pengajuan");
            adminPage.assertVisibleText("Ditolak");
            return pengajuanId;
        }

        adminPage.completePentasPengajuan();
        authPage.loginAsLembaga();
        detailPage.openDetail(pengajuanId);
        detailPage.uploadLaporan();
        authPage.loginAsAdmin();
        adminPage.openAdminPengajuanDetail(pengajuanId);
        adminPage.approveCurrentTimelineStep();
        adminPage.finishPencairan();
        return pengajuanId;
    }
}
