package qa4.reviewconfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import shared.core.BaseTest;

@Tag("qa4")
class Qa4ReviewConfigSmokeTest extends BaseTest {

    @Test
    void shouldOpenSibudayaBaseUrl() {
        openBaseUrl();

        Assertions.assertTrue(driver.getCurrentUrl().contains("sibudaya.cloud"));
        Assertions.assertFalse(driver.getTitle().isBlank());
    }
}
