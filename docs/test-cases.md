# Test Case Sibudaya E2E

SUT: aplikasi web Sibudaya di `https://www.sibudaya.cloud/sibudaya`.

## TC-ADM-001 - CRUD Pengaturan Fasilitasi Pentas

- Tag: `@admin-pentas-crud`
- Aktor: superadmin
- Precondition: superadmin dapat login.
- Langkah: login, buka Pengaturan Fasilitasi, pilih tab Fasilitasi Pentas, tambah jenis fasilitasi, validasi data tampil, edit data, validasi perubahan tampil, hapus data.
- Expected: data pentas berhasil dibuat, dibaca, diperbarui, lalu dihapus.

## TC-ADM-002 - CRUD Pengaturan Fasilitasi Hibah

- Tag: `@admin-hibah-crud`
- Aktor: superadmin
- Precondition: superadmin dapat login.
- Langkah: login, buka Pengaturan Fasilitasi, pilih tab Fasilitasi Hibah, tambah jenis fasilitasi, validasi data tampil, edit data, validasi perubahan tampil, hapus data.
- Expected: data hibah berhasil dibuat, dibaca, diperbarui, lalu dihapus.

## TC-ADM-003 - CRUD Manajemen Pengguna

- Tag: `@admin-user-crud`
- Aktor: superadmin
- Precondition: superadmin dapat login.
- Langkah: login, buka Manajemen Pengguna, tambahkan admin, cari data admin baru, buka detail, edit nama depan, validasi perubahan, hapus admin.
- Expected: akun admin berhasil dibuat, dibaca, diperbarui, lalu dihapus.

## TC-USR-001 - Pengajuan Fasilitasi Pentas

- Tag: `@user-pentas-submit`
- Aktor: user lembaga.
- Precondition: user dapat login dan tidak memiliki pengajuan pentas aktif yang memblokir pengajuan baru.
- Langkah: login, buka Ajukan Fasilitasi, pilih Pentas, isi detail kegiatan, isi administrasi, unggah proposal PDF, kirim pengajuan.
- Expected: halaman status pengajuan tampil setelah submit.

## TC-USR-002 - Pengajuan Fasilitasi Hibah

- Tag: `@user-hibah-submit`
- Aktor: user lembaga.
- Precondition: user dapat login dan tidak memiliki pengajuan hibah aktif yang memblokir pengajuan baru.
- Langkah: login, buka Ajukan Fasilitasi, pilih Hibah, isi detail penerima dan alamat, unggah proposal PDF, kirim pengajuan.
- Expected: halaman status pengajuan tampil setelah submit.

## TC-USR-003 - Update Nama Depan Data Kepala Lembaga

- Tag: `@user-kepala-update`
- Aktor: user lembaga.
- Precondition: user dapat login dan profil lembaga dapat dibuka.
- Langkah: login, buka My Profile, pilih tab Kepala Lembaga, ubah Nama Depan, simpan.
- Expected: sistem menampilkan pesan berhasil dan data kepala lembaga tersimpan.
