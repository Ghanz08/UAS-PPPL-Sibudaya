# Penjelasan File Project UAS PPPL Sibudaya

Dokumen ini menjelaskan fungsi setiap file utama dalam project automation test Sibudaya. Project ini memakai Java, Maven, Selenium WebDriver, Cucumber/Gherkin, JUnit Platform, dan WebDriverManager.

## Ringkasan Arsitektur

Alur kerja test:

1. File `.feature` mendefinisikan skenario dengan bahasa Gherkin.
2. `SibudayaE2eCucumberTest` menjalankan Cucumber melalui JUnit Platform.
3. `SibudayaE2eSteps` memetakan kalimat Gherkin ke kode Java.
4. Step definition memanggil Page Object di folder `pages`.
5. Page Object menjalankan aksi browser melalui Selenium WebDriver.
6. Config, driver, data test, dan lifecycle browser dikelola oleh folder `shared` dan `support`.

## Root Project

### `README.md`

Dokumentasi utama project. File ini menjelaskan tujuan automation test, target aplikasi yang diuji, tech stack, struktur folder, konfigurasi, akun default, dan command untuk menjalankan test berdasarkan tag Cucumber.

### `pom.xml`

File konfigurasi Maven. Fungsinya:

- Menentukan identitas project: `groupId`, `artifactId`, dan `version`.
- Menentukan versi Java yang dipakai, yaitu Java 21.
- Mendefinisikan dependency test seperti JUnit, Cucumber, Selenium, dan WebDriverManager.
- Mengatur `maven-surefire-plugin` agar test bisa dijalankan dengan Maven.

Dependency penting:

- `junit-jupiter`: framework test JUnit.
- `junit-platform-suite`: menjalankan suite test berbasis JUnit Platform.
- `cucumber-java`: binding step definition Cucumber ke Java.
- `cucumber-junit-platform-engine`: menjalankan Cucumber lewat JUnit Platform.
- `cucumber-picocontainer`: dependency injection sederhana untuk sharing context antar step.
- `selenium-java`: otomasi browser.
- `webdrivermanager`: download dan setup ChromeDriver otomatis.

### `.gitignore`

Mengatur file/folder yang tidak perlu masuk Git, seperti hasil build Maven, cache, file IDE, dan file lokal lain.

### `.idea/`

Folder konfigurasi IntelliJ IDEA. Folder ini bukan bagian utama logic automation, tetapi membantu IDE mengenali project.

### `target/`

Folder hasil build Maven. Isinya generated files, compiled classes, dan test reports. Folder ini bisa dihapus karena akan dibuat ulang saat `mvn test` dijalankan.

## Folder `docs`

### `docs/test-cases.md`

Berisi daftar test case yang diuji. Setiap test case memiliki ID test case, tag Cucumber, aktor, precondition, langkah pengujian, dan expected result.

### `docs/bug-report.md`

Berisi catatan bug atau temuan dari hasil pengujian aplikasi Sibudaya. File ini dipakai sebagai laporan pendukung selain automation script.

### `docs/file-explanation.md`

File ini. Berisi penjelasan detail fungsi setiap file dan folder dalam project.

## Folder `outputs`

### `outputs/test-cases/sibudaya-e2e-test-cases.xlsx`

Dokumen Excel test case. Biasanya dipakai untuk kebutuhan laporan, presentasi, atau penilaian yang membutuhkan format spreadsheet.

### `outputs/test-cases/sibudaya-e2e-test-cases-structured.xlsx`

Versi Excel test case yang lebih terstruktur. File ini berguna jika test case perlu dibaca dalam format tabel yang lebih rapi.

## Folder `src/main/java`

### `src/main/java/org/example/Main.java`

File main Java bawaan/template. Project ini berfokus pada automation test, jadi file ini tidak menjadi entry point utama. Entry point test berada di folder `src/test/java`.

## Folder `src/test/resources/shared`

### `src/test/resources/shared/config.properties`

File konfigurasi runtime test. Isinya `base.url`, timeout, akun admin/user, dan flag data test. Nilai config bisa dioverride lewat system property Maven atau environment variable.

Contoh override system property:

```bash
mvn test -Dbase.url="https://www.sibudaya.cloud/sibudaya"
```

Contoh override environment variable:

```powershell
$env:SIBUDAYA_E2E_USER_IDENTIFIER="user-login"
```

## Folder `src/test/resources/features`

Folder ini berisi skenario Cucumber dalam format Gherkin. File `.feature` adalah deskripsi test yang mudah dibaca manusia.

### `admin_manajemen_pengguna_crud.feature`

Skenario CRUD manajemen pengguna oleh superadmin. Alurnya: login, buka manajemen pengguna, buat user/admin baru, cari data, buka detail, ubah data, hapus data.

Tag utama: `@admin-user-crud`.

### `admin_pengaturan_fasilitasi_hibah_crud.feature`

Skenario CRUD pengaturan fasilitasi Hibah. Dipakai untuk memvalidasi superadmin dapat membuat, membaca, mengubah, dan menghapus jenis/paket fasilitasi Hibah.

Tag utama: `@admin-hibah-crud`.

### `admin_pengaturan_fasilitasi_pentas_crud.feature`

Skenario CRUD pengaturan fasilitasi Pentas. Dipakai untuk memvalidasi superadmin dapat membuat, membaca, mengubah, dan menghapus jenis/paket fasilitasi Pentas.

Tag utama: `@admin-pentas-crud`.

### `sibudaya_e2e_readonly.feature`

Skenario E2E read-only. Skenario ini membuka halaman penting tanpa submit data produksi: dashboard user, Ajukan Fasilitasi, status pengajuan, profil user, dashboard superadmin, dan halaman administrasi.

Tag utama: `@readonly`.

### `sibudaya_e2e_submission.feature`

Skenario submit end-to-end. User membuat pengajuan nyata, lalu superadmin mencari pengajuan yang baru dibuat.

Tag utama: `@submit`.

Catatan: tag ini mengubah data aplikasi target.

### `user_pengajuan_hibah.feature`

Skenario user mengajukan Fasilitasi Hibah. Fokusnya pada flow user biasa dari login sampai submit pengajuan Hibah.

Tag utama: `@user-hibah-submit`.

### `user_pengajuan_pentas.feature`

Skenario user mengajukan Fasilitasi Pentas. Fokusnya pada flow user biasa dari login sampai submit pengajuan Pentas.

Tag utama: `@user-pentas-submit`.

### `user_update_kepala_lembaga.feature`

Skenario user update data Kepala Lembaga di halaman profil. Fokus validasinya adalah field nama depan Kepala Lembaga dapat diubah dan disimpan.

Tag utama: `@user-kepala-update`.

## Folder `src/test/resources/sibudaya/e2e`

### `proposal-e2e-sample.pdf`

File PDF sample untuk upload proposal saat menjalankan skenario pengajuan. File ini dipakai oleh `PengajuanFormPage` melalui helper `E2eTestData.proposalPdfPath()`.

## Folder `src/test/java/shared/core`

### `ConfigLoader.java`

Class utility untuk membaca konfigurasi test.

Prioritas pembacaan config:

1. System property Maven, misalnya `-Dbase.url=...`.
2. Environment variable, dengan format uppercase dan titik diganti underscore.
3. File `src/test/resources/shared/config.properties`.

Method penting:

- `get(String key)`: mengambil nilai config.
- `getOptional(String key)`: mengambil nilai config opsional dan mengabaikan placeholder kosong.
- `getBoolean(String key, boolean defaultValue)`: mengambil config boolean.
- `getBaseUrl()`: mengambil URL target aplikasi.
- `getTimeoutSeconds()`: mengambil timeout default.

### `DriverFactory.java`

Factory untuk membuat instance ChromeDriver. Class ini menjalankan setup ChromeDriver otomatis, membuka Chrome maximized, memakai incognito mode, menonaktifkan cache, lalu mengembalikan object `WebDriver`.

### `BaseTest.java`

Base class untuk test JUnit biasa. Class ini menyediakan `setUp()`, `tearDown()`, dan `openBaseUrl()`. Untuk Cucumber, lifecycle browser utama berada di `E2eHooks`.

## Folder `src/test/java/shared/utils`

### `WaitHelper.java`

Helper untuk wait dan pause visual. `defaultWait()` membuat `WebDriverWait` default 30 detik. `pauseForVisual()` memberi jeda 1,5 detik agar aksi browser lebih mudah diamati dan tidak terlalu cepat.

## Folder `src/test/java/sibudaya/e2e`

### `SibudayaE2eCucumberTest.java`

Runner utama Cucumber. File ini mengatur JUnit Platform agar menggunakan engine Cucumber, membaca feature dari folder `features`, memakai glue code package `sibudaya.e2e`, dan menampilkan output `pretty` serta `summary`.

Command yang memakai runner ini:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest
```

## Folder `src/test/java/sibudaya/e2e/support`

### `AuthHelper.java`

Helper login statis untuk login sebagai superadmin atau user biasa. Class ini membungkus pemanggilan `AuthPage`, sehingga test lain bisa login tanpa mengulang detail login.

### `E2eContext.java`

Object context yang dishare antar step Cucumber menggunakan PicoContainer. Data yang disimpan adalah `WebDriver` aktif dan `submissionMarker` untuk pengajuan yang baru dibuat.

### `E2eHooks.java`

Lifecycle hook Cucumber. Sebelum scenario, file ini membuat ChromeDriver dan menyimpannya ke `E2eContext`. Setelah scenario, browser ditutup dengan `driver.quit()`.

### `E2eTestData.java`

Utility data test. Class ini membuat marker unik `AUTO-E2E-yyyyMMddHHmmss`, ID aman untuk email/nomor HP unik, tanggal kegiatan otomatis, dan path PDF proposal.

### `FasilitasiType.java`

Enum tipe fasilitasi yang didukung automation: `PENTAS` dan `HIBAH`. Setiap tipe menyimpan `jenisId` dan `label`. Method `fromLabel()` mengubah input teks dari step menjadi enum.

## Folder `src/test/java/sibudaya/e2e/steps`

### `SibudayaE2eSteps.java`

File step definition Cucumber. Fungsinya menerjemahkan kalimat di `.feature` menjadi aksi Java.

Tanggung jawab utama:

- Validasi credential tersedia.
- Login user biasa atau superadmin.
- Membuka dashboard dan halaman terkait.
- Submit pengajuan.
- CRUD pengaturan fasilitasi.
- CRUD manajemen pengguna.
- Update data Kepala Lembaga.
- Validasi pengajuan terlihat di admin.

Class ini fokus pada bahasa bisnis test. Detail locator dan aksi Selenium berada di Page Object.

## Folder `src/test/java/sibudaya/e2e/pages`

Folder ini berisi Page Object Model. Setiap class mewakili halaman atau area UI tertentu di aplikasi Sibudaya.

### `BaseE2ePage.java`

Base class untuk semua Page Object. Berisi helper umum Selenium: buka path, wait URL, wait URL plus teks halaman, ambil visible text, validasi teks, click link/button, click dalam scope tertentu, isi input by label/name, pilih dropdown, upload PDF, tunggu success message, dan membuat XPath literal aman.

### `AuthPage.java`

Page Object untuk login. Tanggung jawabnya adalah validasi credential, login sebagai user biasa, login sebagai superadmin, membersihkan session setiap ganti role, login dengan retry saat backend mengembalikan `429 Too Many Requests`, menyimpan token per credential agar request login tidak berlebihan, menyalin token ke cookie browser, dan membuka path target setelah login. Validasi dashboard mengecek URL dan teks halaman dashboard.

### `UserDashboardPage.java`

Page Object dashboard user biasa. Class ini memvalidasi dashboard user, membuka Ajukan Fasilitasi, membuka status pengajuan jika ada, membuka profil, dan memberi fallback jika halaman tertentu tidak tersedia. Jika session user terlempar ke halaman login saat fallback status, class ini login ulang sebagai user lalu melanjutkan validasi.

### `AjukanFasilitasiPage.java`

Page Object halaman pemilihan fasilitasi. Class ini memvalidasi halaman Ajukan Fasilitasi, memulai pengajuan pertama yang tersedia, atau memulai pengajuan berdasarkan tipe `PENTAS`/`HIBAH`.

### `PengajuanFormPage.java`

Page Object form pengajuan fasilitasi. Class ini membuat marker unik, mengisi field form sesuai tipe fasilitasi, mengisi tanggal kegiatan, memilih dropdown, upload PDF proposal, submit form, dan mengembalikan marker agar pengajuan bisa dicari oleh admin.

### `StatusPengajuanPage.java`

Page Object halaman status pengajuan. Class ini memvalidasi halaman status atau fallback dashboard, lalu memastikan status pengajuan setelah submit tampil berdasarkan marker.

### `UserProfilePage.java`

Page Object halaman profil user. Class ini membuka profil, menargetkan bagian Kepala Lembaga, mengubah nama depan Kepala Lembaga, menyimpan perubahan, dan menunggu success message.

### `AdminDashboardPage.java`

Page Object dashboard superadmin. Class ini memvalidasi dashboard admin dan membuka atau mencari pengajuan berdasarkan marker yang dibuat user.

### `AdminDataPage.java`

Page Object halaman administrasi read-only. Class ini membuka halaman administrasi dan memvalidasi halaman tersebut tampil.

### `AdminFasilitasiSettingsPage.java`

Page Object pengaturan fasilitasi admin. Class ini membuka halaman pengaturan fasilitasi, memilih tab Pentas/Hibah, membuat data, membaca data, mengubah data, dan menghapus data melalui UI Selenium. Data test memakai marker unik seperti `AUTO-HIBAH-...` dan `AUTO-PENTAS-...`. Setiap tahap CRUD mencetak log detail untuk create, read, update, delete, dan verify deleted.

### `AdminUserManagementPage.java`

Page Object manajemen pengguna admin. Class ini membuka halaman manajemen pengguna, membuat admin baru melalui form UI, mencari user, membuka detail, mengubah nama depan, menghapus user, dan memverifikasi user sudah tidak muncul lagi di UI. Data test selalu unik, termasuk email `admin.<timestamp><random>@gmail.com` dan nomor HP unik. Setiap tahap CRUD mencetak log detail untuk create, read list/detail, update, delete, dan verify deleted.

## Cara Membaca Project Ini

Urutan baca yang disarankan:

1. `README.md`
2. `docs/test-cases.md`
3. File `.feature` yang ingin dipahami
4. `SibudayaE2eSteps.java`
5. Page Object terkait di folder `pages`
6. Helper di folder `support` dan `shared`

Contoh untuk memahami flow pengajuan Hibah:

1. Baca `user_pengajuan_hibah.feature`.
2. Cari step terkait di `SibudayaE2eSteps.java`.
3. Ikuti pemanggilan ke `AuthPage`, `UserDashboardPage`, `AjukanFasilitasiPage`, dan `PengajuanFormPage`.
4. Lihat data test di `E2eTestData.java` dan config akun di `config.properties`.

## Command Penting

Menjalankan semua test:

```bash
mvn test
```

Menjalankan semua E2E Sibudaya:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @e2e"
```

Menjalankan read-only test:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @readonly"
```

Menjalankan submit test:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@sibudaya and @submit"
```

Menjalankan CRUD admin pengaturan Pentas:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@admin-pentas-crud"
```

Menjalankan CRUD admin pengaturan Hibah:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@admin-hibah-crud"
```

Menjalankan CRUD manajemen pengguna:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@admin-user-crud"
```

Menjalankan pengajuan Pentas:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@user-pentas-submit"
```

Menjalankan pengajuan Hibah:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@user-hibah-submit"
```

Menjalankan update Kepala Lembaga:

```bash
mvn test -Dtest=SibudayaE2eCucumberTest -Dcucumber.filter.tags="@user-kepala-update"
```

## Catatan Penting

- Test dengan tag `@submit` membuat data nyata di environment target.
- Gunakan tag `@readonly` jika hanya ingin validasi navigasi tanpa mengubah data.
- Test CRUD admin saat ini benar-benar berjalan lewat UI browser, bukan API langsung.
- Full suite terakhir pada 2026-06-19 berhasil: `8 Scenarios (8 passed)`, `54 Steps (54 passed)`, durasi `13m50,483s`.
- Warning SLF4J provider dan Selenium CDP Chrome 149 bersifat non-fatal pada run final.
- Credential sebaiknya dioverride lewat environment variable jika tidak ingin menyimpan akun asli di repository.
- Folder `target` adalah hasil build, bukan source utama.
- Page Object sebaiknya menjadi tempat utama locator dan aksi Selenium, sedangkan step definition sebaiknya tetap fokus pada bahasa bisnis test.
