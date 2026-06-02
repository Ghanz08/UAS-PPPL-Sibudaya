# PAD Sibudaya QA Automation

Project ini menggunakan:
- Maven
- Selenium WebDriver
- JUnit 5
- Cucumber / Gherkin
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
mvn test -Dtest=qa4.reviewconfig.Qa4CucumberTest -Dcucumber.filter.tags="@qa4"
```

QA-4 memakai BDD dengan Gherkin di `src/test/resources/features/qa4_review_config.feature`
dan Page Object Model di `src/test/java/qa4/reviewconfig/pages`.

## Data Automation QA-4

QA-4 membutuhkan akun dan ID pengajuan live. Jangan simpan credential asli di repo; isi lewat system property saat menjalankan test:

```bash
mvn test -Dtest=qa4.reviewconfig.Qa4CucumberTest -Dcucumber.filter.tags="@qa4" \
  -Dqa4.admin.identifier="admin@example.com" \
  -Dqa4.admin.password="password-admin" \
  -Dqa4.lembaga.identifier="nik-atau-email-lembaga" \
  -Dqa4.lembaga.password="password-lembaga" \
  -Dqa4.timeline.pengajuan.id="id-pengajuan-riwayat-status" \
  -Dqa4.revisi.pengajuan.id="id-pengajuan-direvisi" \
  -Dqa4.selesai.pengajuan.id="id-pengajuan-selesai" \
  -Dqa4.ditolak.pengajuan.id="id-pengajuan-ditolak"
```

Case `M5F1-E01` hanya valid untuk database kosong. Aktifkan khusus environment kosong:

```bash
mvn test -Dtest=qa4.reviewconfig.Qa4CucumberTest -Dcucumber.filter.tags="@M5F1-E01" -Dqa4.empty.dashboard.enabled=true
```

Alternatif env lokal:

1. Copy `.env.qa4.example` menjadi `.env.qa4`.
2. Isi akun admin, akun lembaga, dan ID pengajuan live.
3. Load env di PowerShell:

```powershell
Get-Content .env.qa4 | Where-Object { $_ -and -not $_.StartsWith('#') } | ForEach-Object {
  $name, $value = $_ -split '=', 2
  Set-Item -Path "Env:$name" -Value $value
}
```

4. Jalankan test:

```bash
mvn test -Dtest=qa4.reviewconfig.Qa4CucumberTest -Dcucumber.filter.tags="@qa4"
```

## Catatan

Default browser adalah Chrome normal (non-headless).
