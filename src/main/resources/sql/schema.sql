-- 1. MEMBUAT DATABASE BARU
CREATE DATABASE IF NOT EXISTS chrastis_db_final CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE chrastis_db_final;

-- --------------------------------------------------------

-- 2. MEMBUAT TABEL-TABEL (Struktur tidak berubah)

CREATE TABLE `user` (
                        `id` varchar(20) NOT NULL,
                        `nama` varchar(255) NOT NULL,
                        `password` varchar(255) NOT NULL,
                        `role` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `mahasiswa` (
                             `id` varchar(20) NOT NULL,
                             `ipk` double DEFAULT 0,
                             `tahun_lulus` int(11) NOT NULL,
                             `total_kredit` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `kategori_kredit` (
                                   `id` int(11) NOT NULL,
                                   `nama_kategori` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `sub_kategori_kredit` (
                                       `id` int(11) NOT NULL,
                                       `id_kategori` int(11) NOT NULL,
                                       `nama_sub_kategori` varchar(255) NOT NULL,
                                       `poin` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `pengajuan_kredit` (
                                    `id` varchar(255) NOT NULL,
                                    `mahasiswa_id` varchar(20) NOT NULL,
                                    `id_sub_kategori` int(11) DEFAULT NULL,
                                    `bukti` text DEFAULT NULL,
                                    `tanggal` date NOT NULL,
                                    `status` varchar(50) DEFAULT 'Menunggu',
                                    `nilai` int(11) DEFAULT 0,
                                    `kategori` varchar(255) DEFAULT NULL,
                                    `sub_kategori` varchar(255) DEFAULT NULL,
                                    `deskripsi` text DEFAULT NULL,
                                    `keterangan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `lelang` (
                          `id` varchar(255) NOT NULL,
                          `lokasi` varchar(255) NOT NULL,
                          `minimal_kredit` int(11) NOT NULL,
                          `kuota` int(11) NOT NULL,
                          `tahun_lelang` int(11) NOT NULL,
                          `deskripsi` text DEFAULT NULL,
                          `tanggal_selesai` timestamp NULL DEFAULT NULL,
                          `status` varchar(50) DEFAULT 'Aktif'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `penawaran_lelang` (
                                    `id` varchar(255) NOT NULL,
                                    `lelang_id` varchar(255) NOT NULL,
                                    `mahasiswa_id` varchar(20) NOT NULL,
                                    `jumlah_kredit_ditawar` int(11) NOT NULL,
                                    `tanggal_penawaran` timestamp NOT NULL DEFAULT current_timestamp(),
                                    `status` varchar(50) DEFAULT 'Aktif'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `barang_kopma` (
                                `id` varchar(255) NOT NULL,
                                `nama` varchar(255) NOT NULL,
                                `harga_kredit` int(11) NOT NULL,
                                `stok` int(11) NOT NULL,
                                `deskripsi` text DEFAULT NULL,
                                `kategori` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `transaksi_kopma` (
                                   `id` int(11) NOT NULL,
                                   `mahasiswa_id` varchar(20) NOT NULL,
                                   `barang_id` varchar(255) NOT NULL,
                                   `kode_penukaran` varchar(20) NOT NULL,
                                   `tanggal` date NOT NULL,
                                   `status` varchar(50) DEFAULT 'Menunggu'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `events` (
                          `id` varchar(255) NOT NULL,
                          `title` varchar(255) NOT NULL,
                          `description` text DEFAULT NULL,
                          `image_path` varchar(255) DEFAULT NULL,
                          `event_date` timestamp NULL DEFAULT NULL,
                          `deadline` timestamp NOT NULL,
                          `location` varchar(255) DEFAULT NULL,
                          `status` varchar(50) DEFAULT 'Aktif'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `notifikasi` (
                              `id` varchar(255) NOT NULL,
                              `mahasiswa_id` varchar(20) NOT NULL,
                              `pesan` text NOT NULL,
                              `tanggal` timestamp NOT NULL DEFAULT current_timestamp(),
                              `status` varchar(50) DEFAULT 'Belum Dibaca'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

-- 3. MENAMBAHKAN PRIMARY KEY, AUTO_INCREMENT, DAN INDEX
-- (Struktur tidak berubah)

ALTER TABLE `user` ADD PRIMARY KEY (`id`);
ALTER TABLE `mahasiswa` ADD PRIMARY KEY (`id`);
ALTER TABLE `kategori_kredit` ADD PRIMARY KEY (`id`), MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
ALTER TABLE `sub_kategori_kredit` ADD PRIMARY KEY (`id`), MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;
ALTER TABLE `pengajuan_kredit` ADD PRIMARY KEY (`id`);
ALTER TABLE `lelang` ADD PRIMARY KEY (`id`);
ALTER TABLE `penawaran_lelang` ADD PRIMARY KEY (`id`);
ALTER TABLE `barang_kopma` ADD PRIMARY KEY (`id`);
ALTER TABLE `transaksi_kopma` ADD PRIMARY KEY (`id`), MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `events` ADD PRIMARY KEY (`id`);
ALTER TABLE `notifikasi` ADD PRIMARY KEY (`id`);

-- --------------------------------------------------------

-- 4. MENAMBAHKAN FOREIGN KEY (RELASI ANTAR TABEL)
-- (Struktur tidak berubah)

ALTER TABLE `mahasiswa` ADD CONSTRAINT `fk_mahasiswa_user` FOREIGN KEY (`id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `sub_kategori_kredit` ADD CONSTRAINT `fk_subkategori_kategori` FOREIGN KEY (`id_kategori`) REFERENCES `kategori_kredit` (`id`) ON DELETE CASCADE;
ALTER TABLE `pengajuan_kredit` ADD CONSTRAINT `fk_pengajuan_mahasiswa` FOREIGN KEY (`mahasiswa_id`) REFERENCES `mahasiswa` (`id`), ADD CONSTRAINT `fk_pengajuan_sub_kategori` FOREIGN KEY (`id_sub_kategori`) REFERENCES `sub_kategori_kredit` (`id`) ON DELETE SET NULL;
ALTER TABLE `penawaran_lelang` ADD CONSTRAINT `fk_penawaran_lelang` FOREIGN KEY (`lelang_id`) REFERENCES `lelang` (`id`), ADD CONSTRAINT `fk_penawaran_mahasiswa` FOREIGN KEY (`mahasiswa_id`) REFERENCES `mahasiswa` (`id`);
ALTER TABLE `transaksi_kopma` ADD CONSTRAINT `fk_transaksi_barang` FOREIGN KEY (`barang_id`) REFERENCES `barang_kopma` (`id`), ADD CONSTRAINT `fk_transaksi_mahasiswa` FOREIGN KEY (`mahasiswa_id`) REFERENCES `mahasiswa` (`id`);
ALTER TABLE `notifikasi` ADD CONSTRAINT `fk_notifikasi_mahasiswa` FOREIGN KEY (`mahasiswa_id`) REFERENCES `mahasiswa` (`id`);

-- --------------------------------------------------------

-- 5. MENGISI DATA AWAL (SAMPLE DATA)

-- Isi tabel user
INSERT INTO `user` (`id`, `nama`, `password`, `role`) VALUES
                                                          ('2024001', 'Ahmad Rizki', 'mahasiswa123', 'Mahasiswa'),
                                                          ('2024015', 'Indra Setiawan', 'mahasiswa123', 'Mahasiswa'),
                                                          ('kopma1', 'Administrator KOPMA', 'kopma123', 'AdminKopma'),
                                                          ('kredit1', 'Administrator Kredit', 'kredit123', 'AdminKredit');

-- Isi tabel mahasiswa
INSERT INTO `mahasiswa` (`id`, `ipk`, `tahun_lulus`, `total_kredit`) VALUES
                                                                         ('2024001', 3.8, 2025, 955),
                                                                         ('2024015', 3.7, 2025, 800);

-- Isi tabel kategori_kredit
INSERT INTO `kategori_kredit` (`id`, `nama_kategori`) VALUES
                                                          (1, 'Akademik'), (2, 'Kejuaraan'), (3, 'Organisasi'), (4, 'Workshop');

-- Isi tabel sub_kategori_kredit
INSERT INTO `sub_kategori_kredit` (`id`, `id_kategori`, `nama_sub_kategori`, `poin`) VALUES
                                                                                         (1, 1, 'IPK > 3.75', 1000), (2, 1, 'IPK 3.51 - 3.75', 750), (3, 2, 'Juara 1 Internasional', 500),
                                                                                         (4, 2, 'Juara 2 Internasional', 400), (5, 2, 'Juara 3 Internasional', 300), (6, 2, 'Juara 1 Nasional', 300),
                                                                                         (7, 2, 'Juara 2 Nasional', 250), (8, 2, 'Juara 3 Nasional', 200), (9, 2, 'Juara 1 Kampus', 100),
                                                                                         (10, 2, 'Juara 2 Kampus', 50), (11, 2, 'Juara 3 Kampus', 25), (12, 3, 'Ketua', 100),
                                                                                         (13, 3, 'Wakil Ketua', 75), (14, 3, 'Sekretaris', 50), (15, 3, 'Bendahara', 50),
                                                                                         (16, 3, 'Anggota', 25), (17, 4, 'Seminar', 20);

-- Isi tabel barang_kopma
INSERT INTO `barang_kopma` (`id`, `nama`, `harga_kredit`, `stok`, `deskripsi`, `kategori`) VALUES
                                                                                               ('brg-001', 'Tumbler Keren STIS', 150, 50, 'Tumbler stainless steel tahan panas dan dingin dengan logo STIS.', 'Merchandise'),
                                                                                               ('brg-002', 'Hoodie Angkatan 64', 300, 25, 'Hoodie eksklusif untuk angkatan 64, bahan fleece tebal.', 'Apparel'),
                                                                                               ('brg-003', 'Flashdisk 32GB OTG', 100, 100, 'Flashdisk Sandisk 32GB dengan fitur OTG (On-The-Go).', 'Elektronik');

-- Isi tabel lelang
INSERT INTO `lelang` (`id`, `lokasi`, `minimal_kredit`, `kuota`, `tahun_lelang`, `deskripsi`, `tanggal_selesai`, `status`) VALUES
                                                                                                                               ('lel-001', 'Badan Pusat Statistik - Jakarta Pusat', 800, 5, 2025, 'Penempatan di Direktorat Statistik Kependudukan dan Ketenagakerjaan. Dibutuhkan kemampuan analisis data yang kuat.', '2025-08-30 23:59:59', 'Aktif'),
                                                                                                                               ('lel-002', 'Bappenas - Jakarta Pusat', 900, 2, 2025, 'Penempatan di Direktorat Perencanaan Makro dan Analisis Statistik. Posisi untuk analisis kebijakan.', '2025-09-15 23:59:59', 'Aktif');

-- ==========================================================
-- == DATA EVENT BARU ANDA DITEMPATKAN DI SINI ==
-- ==========================================================
INSERT IGNORE INTO `events` (`id`, `title`, `description`, `image_path`, `event_date`, `deadline`, `location`, `status`) VALUES
('event-001', 'K-Nisaa Reguler', 'Program kajian rutin untuk muslimah dengan tema pengembangan diri dan spiritual bersama ustadzah Kadaniyah.', 'src/main/resources/image/event1.jpg', '2025-06-21 08:30:00', '2025-06-22 23:59:59', 'Masjid Al-Hasanah', 'Selesai'),
('event-002', 'Workshop Programming Advanced', 'Workshop intensif untuk meningkatkan kemampuan programming dengan teknologi terbaru. Cocok untuk mahasiswa tingkat lanjut.', 'src/main/resources/image/event2.jpg', '2025-06-25 13:00:00', '2025-06-26 23:59:59', 'Lab Komputer', 'Selesai'),
('event-003', 'Seminar AI and Machine Learning', 'Seminar tentang perkembangan AI dan Machine Learning dengan pembicara dari industri teknologi terkemuka.', 'src/main/resources/image/event3.jpg', '2025-07-10 09:00:00', '2025-07-11 23:59:59', 'Auditorium', 'Aktif'),
('event-004', 'Career Fair and Job Expo 2025', 'Pameran karir dengan berbagai perusahaan teknologi terkemuka. Kesempatan emas untuk networking dan mencari pekerjaan.', 'src/main/resources/image/event4.jpg', '2025-07-15 08:00:00', '2025-07-16 23:59:59', 'Hall Utama', 'Aktif'),
('event-005', 'Student Achievement Awards', 'Malam penghargaan untuk mengapresiasi mahasiswa berprestasi dalam berbagai bidang akademik dan non-akademik.', 'src/main/resources/image/event5.jpg', '2025-07-20 19:00:00', '2025-07-21 23:59:59', 'Ballroom', 'Aktif');

-- MENAMBAHKAN DATA MAHASISWA BARU
-- Pertama, tambahkan ke tabel 'user'
INSERT INTO `user` (`id`, `nama`, `password`, `role`) VALUES
                                                          ('2024002', 'Siti Nurhaliza', 'mahasiswa123', 'Mahasiswa'),
                                                          ('2024003', 'Budi Santoso', 'mahasiswa123', 'Mahasiswa'),
                                                          ('2024004', 'Dewi Lestari', 'mahasiswa123', 'Mahasiswa');

-- Kedua, tambahkan detailnya ke tabel 'mahasiswa'
INSERT INTO `mahasiswa` (`id`, `ipk`, `tahun_lulus`, `total_kredit`) VALUES
                                                                         ('2024002', 3.91, 2025, 1250),
                                                                         ('2024003', 3.65, 2026, 780),
                                                                         ('2024004', 3.77, 2026, 920);

-- --------------------------------------------------------

-- MENAMBAHKAN DATA BARANG KOPMA BARU
INSERT INTO `barang_kopma` (`id`, `nama`, `harga_kredit`, `stok`, `deskripsi`, `kategori`) VALUES
                                                                                               ('brg-004', 'Buku Tulis STIS (Isi 5)', 50, 200, 'Satu pak berisi 5 buku tulis berkualitas dengan sampul eksklusif STIS.', 'Alat Tulis'),
                                                                                               ('brg-005', 'Kaos Kaki Logo STIS', 75, 80, 'Kaos kaki nyaman dengan bahan katun dan bordir logo STIS.', 'Apparel'),
                                                                                               ('brg-006', 'Gantungan Kunci Akrilik', 30, 150, 'Gantungan kunci akrilik dengan maskot PST (Pusat Statistik Terpadu).', 'Aksesoris');

-- --------------------------------------------------------

-- MENAMBAHKAN DATA LELANG PENEMPATAN BARU
INSERT INTO `lelang` (`id`, `lokasi`, `minimal_kredit`, `kuota`, `tahun_lelang`, `deskripsi`, `tanggal_selesai`, `status`) VALUES
                                                                                                                               ('lel-003', 'BPS Provinsi Jawa Barat - Bandung', 750, 10, 2025, 'Penempatan di BPS Provinsi Jawa Barat. Dibutuhkan talenta muda yang dinamis dan siap belajar.', '2025-08-25 23:59:59', 'Aktif'),
                                                                                                                               ('lel-004', 'Kementerian Keuangan - Jakarta', 950, 3, 2025, 'Posisi analis data di Pusat Kebijakan Sektor Keuangan (PKSK). Memerlukan nilai kredit yang sangat tinggi.', '2025-09-10 23:59:59', 'Aktif'),
                                                                                                                               ('lel-005', 'BPS Provinsi Jawa Timur - Surabaya', 700, 8, 2026, 'Peluang penempatan untuk lulusan tahun 2026 di ibu kota provinsi Jawa Timur.', '2026-08-20 23:59:59', 'Aktif');

-- MENAMBAHKAN DATA MAHASISWA BARU DENGAN KREDIT TINGGI

-- Langkah 1: Tambahkan data login ke tabel 'user'
INSERT INTO `user` (`id`, `nama`, `password`, `role`) VALUES
                                                          ('2024005', 'Rian Hidayat', 'mahasiswa123', 'Mahasiswa'),
                                                          ('2024006', 'Putri Anggraini', 'mahasiswa123', 'Mahasiswa'),
                                                          ('2024007', 'Agung Nugroho', 'mahasiswa123', 'Mahasiswa'),
                                                          ('2024008', 'Fitriani', 'mahasiswa123', 'Mahasiswa');

-- Langkah 2: Tambahkan detail mahasiswa ke tabel 'mahasiswa'
INSERT INTO `mahasiswa` (`id`, `ipk`, `tahun_lulus`, `total_kredit`) VALUES
                                                                         ('2024005', 3.85, 2025, 2150),
                                                                         ('2024006', 3.92, 2025, 3400),
                                                                         ('2024007', 3.78, 2026, 4850),
                                                                         ('2024008', 3.88, 2026, 2900);

COMMIT;