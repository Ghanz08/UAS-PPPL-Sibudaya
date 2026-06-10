package sibudaya.e2e.pages;

import org.openqa.selenium.WebDriver;

public class StatusPengajuanPage extends BaseE2ePage {
    public StatusPengajuanPage(WebDriver driver) {
        super(driver);
    }

    public void assertStatusOrDashboardShown() {
        assertVisibleAnyText("Status", "Pengajuan", "Timeline", "Dashboard", "Pantau perkembangan");
    }

    public void assertSubmittedStatusShown(String marker) {
        waitForUrlContains("/dashboard/status/");
        assertVisibleAnyText("Status", "Pengajuan", "Timeline", marker, "Menunggu", "Diajukan");
    }
}
