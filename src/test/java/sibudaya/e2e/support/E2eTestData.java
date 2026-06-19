package sibudaya.e2e.support;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class E2eTestData {
    private E2eTestData() {
    }

    public static String marker() {
        return "AUTO-E2E-" + safeId();
    }

    public static String safeId() {
        return DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now())
                + "-" + Integer.toUnsignedString(ThreadLocalRandom.current().nextInt(), 36);
    }

    public static String uniqueEmail(String prefix) {
        return prefix + "." + safeId().replaceAll("[^a-zA-Z0-9]", "") + "@gmail.com";
    }

    public static String uniquePhone() {
        long number = ThreadLocalRandom.current().nextLong(1_000_000_000L, 9_999_999_999L);
        return "08" + number;
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
