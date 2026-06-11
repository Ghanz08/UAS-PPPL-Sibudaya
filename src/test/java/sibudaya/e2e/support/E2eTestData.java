package sibudaya.e2e.support;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class E2eTestData {
    private E2eTestData() {
    }

    public static String marker() {
        return "AUTO-E2E-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
    }

    public static LocalDate eventDate() {
        return LocalDate.now().plusDays(30);
    }

    public static Path proposalPdfPath() {
        try {
            return Paths.get(Objects.requireNonNull(
                    E2eTestData.class.getClassLoader().getResource("sibudaya/e2e/proposal-e2e-sample.pdf"),
                    "Missing proposal-e2e-sample.pdf"
            ).toURI()).toAbsolutePath();
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Invalid proposal fixture path", exception);
        }
    }
}
