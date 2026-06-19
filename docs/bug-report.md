# UAT Bug Report - Sibudaya

Tanggal UAT: 2026-06-19
Environment: Production UAT `https://www.sibudaya.cloud/sibudaya`
Metode: UAT berbasis skenario end-to-end dengan Selenium, Cucumber, dan JUnit Platform

## Akun UAT

| Role | Username | Password |
| --- | --- | --- |
| Superadmin | `superadmin@fasilitasi.go.id` | `SuperAdmin@2026!` |
| User lembaga | `NIK-LBG-2026-0001` | `12345678` |

## Ringkasan Hasil UAT

| ID UAT | Skenario | Status |
| --- | --- | --- |
| UAT-ADM-001 | CRUD Pengaturan Fasilitasi Pentas | Pass |
| UAT-ADM-002 | CRUD Pengaturan Fasilitasi Hibah | Pass |
| UAT-ADM-003 | CRUD Manajemen Pengguna | Pass |
| UAT-USR-001 | User melakukan pengajuan Pentas | Pass |
| UAT-USR-002 | User melakukan pengajuan Hibah | Pass |
| UAT-USR-003 | User mengganti nama depan Data Kepala Lembaga | Pass |

Hasil eksekusi automation UAT:

```text
6 Scenarios (6 passed)
30 Steps (30 passed)
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
Durasi: 3m52,010s
```

Catatan automation: CRUD admin user, CRUD pengaturan fasilitasi Hibah, dan CRUD pengaturan fasilitasi Pentas dijalankan melalui UI Selenium. Log eksekusi mencetak data unik yang dibuat, dibaca, diubah, dihapus, dan diverifikasi hilang dari UI.

## Daftar Defect UAT

### UAT-BUG-001 - Tidak ada defect blocking pada flow utama UAT

- Severity: None
- Priority: None
- Status: Closed
- Skenario terkait: seluruh skenario UAT
- Expected result: seluruh flow utama admin dan user dapat dijalankan sampai selesai.
- Actual result: seluruh flow utama berhasil dijalankan.
- Evidence: hasil automation UAT menunjukkan `6 Scenarios (6 passed)` dan `Skipped: 0`.
- Kesimpulan: tidak ditemukan defect blocking atau critical pada scope UAT yang diuji.

## Temuan Minor / Improvement UAT

### UAT-OBS-001 - Pesan kondisi tidak bisa mengajukan fasilitasi perlu dibuat lebih eksplisit

- Severity: Low
- Priority: Medium
- Status: Open
- Skenario terkait: UAT-USR-001, UAT-USR-002
- Expected result: jika user belum bisa mengajukan Pentas/Hibah, aplikasi menjelaskan penyebabnya secara spesifik, misalnya masih ada pengajuan aktif, kuota habis, periode belum dibuka, atau jenis fasilitasi belum tersedia.
- Actual result: pada state tertentu, tombol/link pengajuan dapat tidak tersedia sehingga user perlu menafsirkan sendiri penyebabnya.
- Impact UAT: tidak memblokir submit pada run final, tetapi dapat membingungkan user saat data akun/kuota berubah.
- Recommendation: tampilkan disabled reason atau alert pada kartu fasilitasi.

### UAT-OBS-002 - Halaman Pengaturan Fasilitasi perlu loading/empty state yang lebih jelas

- Severity: Low
- Priority: Medium
- Status: Open
- Skenario terkait: UAT-ADM-001, UAT-ADM-002
- Expected result: halaman Pengaturan Fasilitasi menampilkan heading, tab Pentas/Hibah, loading state, empty state, dan error state secara konsisten.
- Actual result: pada beberapa akses, render heading/tab dapat terasa lambat sebelum data siap.
- Impact UAT: tidak memblokir CRUD pada run final, tetapi dapat membuat admin mengira halaman belum siap atau kosong.
- Recommendation: tambahkan loading indicator dan empty state yang eksplisit pada tab Pentas dan Hibah.

### UAT-OBS-003 - Elemen penting belum memiliki identifier stabil untuk regression UAT

- Severity: Low
- Priority: Low
- Status: Open
- Skenario terkait: seluruh automation UAT
- Expected result: elemen penting memiliki identifier stabil untuk regression test, misalnya `data-testid`.
- Actual result: automation masih mengandalkan label, teks tombol, placeholder, dan struktur DOM.
- Impact UAT: perubahan copy UI dapat membuat script regression perlu diperbarui walaupun fungsi aplikasi tetap benar.
- Recommendation: tambahkan `data-testid` pada login input, tombol login, menu admin, tab fasilitasi, tombol tambah/edit/hapus, kartu pengajuan, form submit, dan field Data Kepala Lembaga.

### UAT-OBS-004 - Endpoint login production dapat mengembalikan 429 saat regression panjang

- Severity: Low
- Priority: Medium
- Status: Mitigated in automation
- Skenario terkait: seluruh skenario yang berganti role user/superadmin
- Expected result: automation dapat login secara stabil selama regression suite penuh.
- Actual result: backend sesekali mengembalikan `429 Too Many Requests` pada `/api/v1/auth/login` jika login dipanggil terlalu sering.
- Impact UAT: dapat membuat test flakey jika tidak ada retry dan token reuse.
- Mitigation: automation membersihkan session saat ganti role, memakai token cache per credential, retry login dengan jeda, dan memvalidasi dashboard berdasarkan URL plus teks dashboard.
- Recommendation: sediakan mode test-friendly rate limit untuk akun UAT atau token/session bootstrap khusus regression.

## Keputusan UAT

Status UAT: Accepted with minor observations.

Alasan: seluruh skenario dalam scope UAT berhasil dijalankan tanpa failed dan tanpa skipped. Temuan yang tersisa bersifat improvement UX/testability, bukan defect yang memblokir flow utama.
