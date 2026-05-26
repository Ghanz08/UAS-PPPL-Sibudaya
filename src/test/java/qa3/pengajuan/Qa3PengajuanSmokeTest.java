package qa3.pengajuan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import shared.core.BaseTest;

@Tag("qa3")
class Qa3PengajuanSmokeTest extends BaseTest {

    @Test
    void shouldOpenSibudayaBaseUrl() {
        openBaseUrl();

        Assertions.assertTrue(driver.getCurrentUrl().contains("sibudaya.cloud"));
        Assertions.assertFalse(driver.getTitle().isBlank());
    }
}
