package qa2.adminkelola;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import shared.core.BaseTest;

@Tag("qa2")
class Qa2AdminKelolaSmokeTest extends BaseTest {

    @Test
    void shouldOpenSibudayaBaseUrl() {
        openBaseUrl();

        Assertions.assertTrue(driver.getCurrentUrl().contains("sibudaya.cloud"));
        Assertions.assertFalse(driver.getTitle().isBlank());
    }
}
