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

Hasil verifikasi terakhir pada 2026-06-19:

```text
8 Scenarios (8 passed)
54 Steps (54 passed)
Durasi: 13m50,483s
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
|   +-- sibudaya_e2e_readonly.feature
|   +-- sibudaya_e2e_submission.feature
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

Konfigurasi default ada di:

```text
src/test/resources/shared/config.properties
```

Nilai konfigurasi bisa dioverride dengan system property Maven:

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

Key yang umum dipakai:

```text
base.url
default.timeout.seconds
sibudaya.e2e.user.identifier
sibudaya.e2e.user.password
sibudaya.e2e.superadmin.identifier
sibudaya.e2e.superadmin.password
```

Default akun assignment:

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

Flow read-only:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @readonly"
```

Flow submit data production:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @submit"
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

Peringatan: tag `@submit` membuat pengajuan fasilitasi nyata di environment target. Gunakan `@readonly` jika tidak boleh mengubah data production.

## Skenario Yang Tersedia

### Read-only navigation

File:

```text
src/test/resources/features/sibudaya_e2e_readonly.feature
```

Alur utama:

1. User biasa login.
2. Dashboard user terbuka.
3. Halaman pilihan fasilitasi terbuka tanpa submit data.
4. Halaman status pengajuan dibuka jika ada data.
5. Halaman profil user terbuka.
6. Superadmin login.
7. Dashboard superadmin terbuka.
8. Halaman administrasi read-only terbuka.

### Real submission end-to-end

File:

```text
src/test/resources/features/sibudaya_e2e_submission.feature
```

Alur utama:

1. User biasa login.
2. User membuka halaman pilihan fasilitasi.
3. User memilih fasilitasi pertama yang tersedia.
4. User mengisi dan mengirim form pengajuan.
5. Status pengajuan tampil.
6. Superadmin login.
7. Superadmin mencari pengajuan yang baru dibuat.
8. Pengajuan terlihat di sisi superadmin.

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
