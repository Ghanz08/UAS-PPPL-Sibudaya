# PAD Sibudaya QA Automation

Project ini menggunakan:
- Maven
- Selenium WebDriver
- JUnit 5
- WebDriverManager

## URL Frontend

`https://www.sibudaya.cloud/sibudaya`

## Struktur Automation

- `src/test/java/qa1/authakses`
- `src/test/java/qa2/adminkelola`
- `src/test/java/qa3/pengajuan`
- `src/test/java/qa4/reviewconfig`
- `src/test/java/shared`

Setiap QA bekerja di package masing-masing agar tidak saling mengganggu.

## Menjalankan Test

Semua test (sequential):

```bash
mvn test
```

Per QA berdasarkan tag:

```bash
mvn test -Dgroups=qa1
mvn test -Dgroups=qa2
mvn test -Dgroups=qa3
mvn test -Dgroups=qa4
```

## Catatan

Default browser adalah Chrome normal (non-headless).
