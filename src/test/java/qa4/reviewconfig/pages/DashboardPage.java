package qa4.reviewconfig.pages;

import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {
    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public void openAdminDashboard() {
        openAuthenticatedPath("/dashboard/admin");
    }

    public void assertAdminSummaryShown() {
        assertVisibleText("Dashboard");
        assertVisibleText("Total Pengajuan");
        assertVisibleText("Status Pengajuan Fasilitasi");
        assertVisibleText("NIK Terdaftar");
        assertVisibleText("Terbaru");
        assertVisibleAnyText("Selesai", "Dalam Proses", "Perlu Tindakan", "Perlu Revisi", "Ditolak");
    }

    public void assertEmptyDashboardShownWithoutBackendError() {
        assertVisibleText("Total Pengajuan");
        assertVisibleText("0");
        assertVisibleAnyText("Belum Ada Pengajuan Fasilitasi", "Belum ada data", "Belum ada permohonan");
        assertNoVisibleText("Gagal meneruskan request ke backend");
        assertNoVisibleText("Tidak bisa menghubungi backend API");
        assertNoVisibleText("Sesi telah berakhir");
    }
}
