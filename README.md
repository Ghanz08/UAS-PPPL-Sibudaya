# UAS PPPL Sibudaya QA Automation

Automation test untuk aplikasi Sibudaya berbasis Java, Maven, Selenium WebDriver, JUnit Platform, dan Cucumber/Gherkin.

Target default:

```text
https://www.sibudaya.cloud/sibudaya
```

## SUT

System Under Test adalah aplikasi web Sibudaya, layanan fasilitasi lembaga budaya untuk pengajuan Fasilitasi Pentas dan Fasilitasi Hibah. Area yang diuji mencakup login user lembaga, pengajuan fasilitasi, profil lembaga, pengaturan fasilitasi admin, dan manajemen pengguna admin.

## Test Suite

Test suite memakai Selenium WebDriver dengan Page Object Model, Cucumber/Gherkin, dan JUnit Platform. Login admin dan user dipusatkan di helper `AuthHelper` sehingga setiap flow bisa memakai kredensial yang sama tanpa duplikasi step.

CRUD admin dijalankan dari UI browser, bukan lewat API langsung. Test mengisi form, mencari data di tabel/list, membuka detail, mengubah data, menghapus data, lalu memverifikasi data sudah hilang dari UI. Log eksekusi juga mencetak data unik yang dibuat, dibaca, diubah, dan dihapus agar hasil CRUD mudah diaudit.

Flow utama:

- Admin CRUD Pengaturan Fasilitasi Pentas: `@admin-pentas-crud`
- Admin CRUD Pengaturan Fasilitasi Hibah: `@admin-hibah-crud`
- Admin CRUD Manajemen Pengguna: `@admin-user-crud`
- User Pengajuan Pentas: `@user-pentas-submit`
- User Pengajuan Hibah: `@user-hibah-submit`
- User Update Data Kepala Lembaga: `@user-kepala-update`

Hasil verifikasi setelah dua flow umum dihapus dari suite:

```text
6 Scenarios (6 passed)
30 Steps (30 passed)
```

Dokumen test case dan bug report tersedia di:

```text
docs/test-cases.md
docs/bug-report.md
```


## Tech Stack

- Java 21
- Maven
- Selenium WebDriver 4
- WebDriverManager
- Cucumber 7
- JUnit Platform Suite
- Chrome Browser

## Struktur Project

```text
src/test/java
+-- shared
|   +-- core        # konfigurasi, driver, base test
|   +-- utils       # helper wait
+-- sibudaya/e2e
    +-- pages       # Page Object Model
    +-- steps       # Step definitions Cucumber
    +-- support     # hooks, context, data test

src/test/resources
+-- features
|   +-- admin_manajemen_pengguna_crud.feature
|   +-- admin_pengaturan_fasilitasi_hibah_crud.feature
|   +-- admin_pengaturan_fasilitasi_pentas_crud.feature
|   +-- user_pengajuan_hibah.feature
|   +-- user_pengajuan_pentas.feature
|   +-- user_update_kepala_lembaga.feature
+-- shared/config.properties
+-- sibudaya/e2e/proposal-e2e-sample.pdf

docs
+-- test-cases.md
+-- bug-report.md
```

## Prasyarat

Pastikan tersedia di mesin lokal:

```bash
java -version
mvn -version
```

Project dikompilasi dengan Java 21. Test berjalan memakai Chrome normal dengan mode incognito, cache disabled, dan window maximized. ChromeDriver dikelola otomatis oleh WebDriverManager.

## Konfigurasi

Konfigurasi default ikut repository, jadi setelah clone/pull file ini sudah tersedia dan test bisa langsung dijalankan tanpa membuat file config baru:

```text
src/test/resources/shared/config.properties
```

Nilai di file tersebut adalah default bersama untuk UAT. Jika perlu menjalankan target atau akun lain, override dengan system property Maven:

```bash
mvn test -Dbase.url="https://www.sibudaya.cloud/sibudaya"
```

Atau environment variable dengan huruf besar dan titik diganti underscore:

```powershell
$env:SIBUDAYA_E2E_USER_IDENTIFIER="user-login"
$env:SIBUDAYA_E2E_USER_PASSWORD="password-user"
$env:SIBUDAYA_E2E_SUPERADMIN_IDENTIFIER="superadmin-login"
$env:SIBUDAYA_E2E_SUPERADMIN_PASSWORD="password-superadmin"
```

Atau pakai file config lain tanpa mengubah file default repository:

```bash
mvn test -Dconfig.file="C:/path/to/config.properties"
```

Key yang umum dipakai:

```text
base.url
default.timeout.seconds
sibudaya.e2e.user.identifier
sibudaya.e2e.user.password
sibudaya.e2e.superadmin.identifier
sibudaya.e2e.superadmin.password
```

Default akun assignment sudah ada di `config.properties`:

```text
Admin/superadmin: superadmin@fasilitasi.go.id / SuperAdmin@2026!
User lembaga: NIK-LBG-2026-0001 / 12345678
```

## Menjalankan Test

Semua test:

```bash
mvn test
```

Semua flow Sibudaya E2E:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @e2e"
```

Flow admin pengaturan fasilitasi pentas:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@admin-pentas-crud"
```

Flow admin pengaturan fasilitasi hibah:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@admin-hibah-crud"
```

Flow admin manajemen pengguna:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@admin-user-crud"
```

Flow user pengajuan pentas:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@user-pentas-submit"
```

Flow user pengajuan hibah:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@user-hibah-submit"
```

Flow user update Data Kepala Lembaga:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@user-kepala-update"
```

Peringatan: tag submit pengajuan membuat data fasilitasi nyata di environment target.

## Skenario Yang Tersedia

## Data Test

Flow submit memakai fixture PDF:

```text
src/test/resources/sibudaya/e2e/proposal-e2e-sample.pdf
```

Marker data otomatis memakai format:

```text
AUTO-E2E-yyyyMMddHHmmss
```

Tanggal kegiatan otomatis diset 30 hari dari tanggal eksekusi test.

Data CRUD admin memakai suffix unik per eksekusi, misalnya email `admin.<timestamp><random>@gmail.com`, nomor HP unik, dan marker fasilitasi `AUTO-HIBAH-...` atau `AUTO-PENTAS-...`.

## Stabilitas E2E

- Session browser dibersihkan saat berganti role.
- Login memiliki retry jika backend mengembalikan `429 Too Many Requests`.
- Token login dicache per credential untuk mengurangi hit ke endpoint login.
- Validasi dashboard mengecek URL dan teks dashboard, bukan URL saja.
- Jika session user terlempar ke halaman login saat fallback status pengajuan, test login ulang sebagai user dan melanjutkan validasi.
