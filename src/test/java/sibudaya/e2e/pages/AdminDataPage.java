package sibudaya.e2e.pages;

import org.openqa.selenium.WebDriver;

public class AdminDataPage extends BaseE2ePage {
    public AdminDataPage(WebDriver driver) {
        super(driver);
    }

    public void openReadOnlyAdminPage() {
        openPath("/dashboard/admin/lembaga-budaya");
        if (!driver.getCurrentUrl().contains("/dashboard/admin/lembaga-budaya")) {
            openPath("/dashboard/admin/pengaturan-fasilitasi");
        }
    }

    public void assertShown() {
        assertVisibleAnyText("Lembaga", "Pengaturan Fasilitasi", "Manajemen", "Data", "Dashboard");
    }
}
