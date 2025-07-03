package com.chrastis.service;

import com.chrastis.data.DatabaseConnection;
import com.chrastis.model.Lelang;
import com.chrastis.model.Mahasiswa;
import com.chrastis.model.PenawaranLelang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chrastis.model.LelangPendaftarInfo;

public class LelangService {
    private static final Logger LOGGER = Logger.getLogger(LelangService.class.getName());
    private final DatabaseConnection dbConnection = new DatabaseConnection();
    private final UserService userService = new UserService();
    private final NotifikasiService notifikasiService = new NotifikasiService();

    // <editor-fold desc="Metode GET (Mengambil Data)">
    public List<Lelang> getAllLelang() {
        List<Lelang> lelangList = new ArrayList<>();
        String sql = "SELECT * FROM lelang ORDER BY tahun_lelang DESC, tanggal_selesai DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lelangList.add(mapRowToLelang(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all lelang", e);
        }
        return lelangList;
    }

    public Optional<Lelang> getLelangById(String lelangId) {
        String sql = "SELECT * FROM lelang WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lelangId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToLelang(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching lelang by ID", e);
        }
        return Optional.empty();
    }

    public List<Lelang> getLelangAktif() {
        List<Lelang> lelangList = new ArrayList<>();
        String sql = "SELECT * FROM lelang WHERE status = 'Aktif'";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lelangList.add(mapRowToLelang(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching lelang aktif", e);
        }
        return lelangList;
    }

    public List<PenawaranLelang> getPendaftarByLelangId(String lelangId) {
        List<PenawaranLelang> pendaftar = new ArrayList<>();
        String sql = "SELECT pl.*, u.id as user_id, u.nama, m.ipk, m.tahun_lulus, m.total_kredit " +
                "FROM penawaran_lelang pl " +
                "JOIN user u ON pl.mahasiswa_id = u.id " +
                "JOIN mahasiswa m ON u.id = m.id " +
                "WHERE pl.lelang_id = ? " +
                "ORDER BY pl.jumlah_kredit_ditawar DESC, pl.tanggal_penawaran ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lelangId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Mahasiswa m = new Mahasiswa(rs.getString("user_id"), rs.getString("nama"), rs.getDouble("ipk"), rs.getInt("tahun_lulus"), rs.getInt("total_kredit"));
                PenawaranLelang p = new PenawaranLelang(rs.getString("id"), null, m, rs.getInt("jumlah_kredit_ditawar"), rs.getTimestamp("tanggal_penawaran").toLocalDateTime(), rs.getString("status"));
                pendaftar.add(p);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching pendaftar for lelang " + lelangId, e);
        }
        return pendaftar;
    }

    public Optional<PenawaranLelang> getPenawaranAktifMahasiswa(String mahasiswaId) {
        String sql = "SELECT pl.*, l.lokasi FROM penawaran_lelang pl JOIN lelang l ON pl.lelang_id = l.id WHERE pl.mahasiswa_id = ? AND pl.status = 'Aktif'";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mahasiswaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Lelang lelangInfo = new Lelang(rs.getString("lelang_id"), rs.getString("lokasi"), 0, 0, 0, null, null, "Aktif");
                PenawaranLelang penawaran = new PenawaranLelang(rs.getString("id"), lelangInfo, null, rs.getInt("jumlah_kredit_ditawar"), rs.getTimestamp("tanggal_penawaran").toLocalDateTime(), rs.getString("status"));
                return Optional.of(penawaran);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching penawaran aktif by mahasiswa", e);
        }
        return Optional.empty();
    }
    // </editor-fold>

    // <editor-fold desc="Metode CUD (Create, Update, Delete)">
    public boolean createLelang(Lelang lelang) {
        String sql = "INSERT INTO lelang (id, lokasi, deskripsi, minimal_kredit, kuota, tahun_lelang, tanggal_selesai, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'Aktif')";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lelang.getId());
            stmt.setString(2, lelang.getLokasi());
            stmt.setString(3, lelang.getDeskripsi());
            stmt.setInt(4, lelang.getMinimalKredit());
            stmt.setInt(5, lelang.getKuota());
            stmt.setInt(6, lelang.getTahunLelang());
            stmt.setTimestamp(7, Timestamp.valueOf(lelang.getTanggalSelesai()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating lelang", e);
            return false;
        }
    }

    public boolean updateLelang(Lelang lelang) {
        String sql = "UPDATE lelang SET lokasi = ?, deskripsi = ?, minimal_kredit = ?, kuota = ?, tahun_lelang = ?, tanggal_selesai = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lelang.getLokasi());
            stmt.setString(2, lelang.getDeskripsi());
            stmt.setInt(3, lelang.getMinimalKredit());
            stmt.setInt(4, lelang.getKuota());
            stmt.setInt(5, lelang.getTahunLelang());
            stmt.setTimestamp(6, Timestamp.valueOf(lelang.getTanggalSelesai()));
            stmt.setString(7, lelang.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating lelang", e);
            return false;
        }
    }

    public boolean tambahPenawaran(String lelangId, String mahasiswaId, int jumlahBid) {
        String findOldBidSql = "SELECT jumlah_kredit_ditawar FROM penawaran_lelang WHERE mahasiswa_id = ?";
        String deleteOldBidSql = "DELETE FROM penawaran_lelang WHERE mahasiswa_id = ?";
        String insertNewBidSql = "INSERT INTO penawaran_lelang (id, lelang_id, mahasiswa_id, jumlah_kredit_ditawar) VALUES (?, ?, ?, ?)";
        String updateUserKreditSql = "UPDATE mahasiswa SET total_kredit = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int kreditLama = 0;
                try (PreparedStatement findStmt = conn.prepareStatement(findOldBidSql)) {
                    findStmt.setString(1, mahasiswaId);
                    ResultSet rs = findStmt.executeQuery();
                    if (rs.next()) kreditLama = rs.getInt("jumlah_kredit_ditawar");
                }
                if (kreditLama > 0) {
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteOldBidSql)) {
                        deleteStmt.setString(1, mahasiswaId);
                        deleteStmt.executeUpdate();
                    }
                }
                Mahasiswa m = (Mahasiswa) userService.getUserById(mahasiswaId).orElseThrow(() -> new SQLException("Mahasiswa tidak ditemukan"));
                int kreditAwal = m.getTotalKredit() + kreditLama;
                if (jumlahBid > kreditAwal) throw new SQLException("Kredit tidak cukup");
                int kreditAkhir = kreditAwal - jumlahBid;
                try (PreparedStatement updateKreditStmt = conn.prepareStatement(updateUserKreditSql)) {
                    updateKreditStmt.setInt(1, kreditAkhir);
                    updateKreditStmt.setString(2, mahasiswaId);
                    updateKreditStmt.executeUpdate();
                }
                try (PreparedStatement insertStmt = conn.prepareStatement(insertNewBidSql)) {
                    insertStmt.setString(1, UUID.randomUUID().toString());
                    insertStmt.setString(2, lelangId);
                    insertStmt.setString(3, mahasiswaId);
                    insertStmt.setInt(4, jumlahBid);
                    insertStmt.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Gagal menambah penawaran, transaksi di-rollback.", e);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Koneksi database gagal saat transaksi penawaran.", e);
            return false;
        }
    }

    public boolean tarikPenawaran(String mahasiswaId) {
        String findBidSql = "SELECT jumlah_kredit_ditawar FROM penawaran_lelang WHERE mahasiswa_id = ?";
        String deleteBidSql = "DELETE FROM penawaran_lelang WHERE mahasiswa_id = ?";
        String updateUserKreditSql = "UPDATE mahasiswa SET total_kredit = total_kredit + ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int kreditUntukDikembalikan = 0;
                try (PreparedStatement findStmt = conn.prepareStatement(findBidSql)) {
                    findStmt.setString(1, mahasiswaId);
                    ResultSet rs = findStmt.executeQuery();
                    if (rs.next()) {
                        kreditUntukDikembalikan = rs.getInt("jumlah_kredit_ditawar");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
                try (PreparedStatement updateKreditStmt = conn.prepareStatement(updateUserKreditSql)) {
                    updateKreditStmt.setInt(1, kreditUntukDikembalikan);
                    updateKreditStmt.setString(2, mahasiswaId);
                    updateKreditStmt.executeUpdate();
                }
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteBidSql)) {
                    deleteStmt.setString(1, mahasiswaId);
                    deleteStmt.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Gagal menarik penawaran, transaksi di-rollback.", e);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Koneksi database gagal saat menarik penawaran.", e);
            return false;
        }
    }
    // </editor-fold>

    // <editor-fold desc="Metode Aksi Admin">
    public boolean finalizeLelang(String lelangId) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Lelang lelang = getLelangById(lelangId).orElseThrow(() -> new SQLException("Lelang tidak ditemukan"));
                if (!"Aktif".equals(lelang.getStatus())) throw new SQLException("Lelang sudah tidak aktif.");
                int kuota = lelang.getKuota();
                List<PenawaranLelang> pendaftar = getPendaftarByLelangId(lelangId);

                for (int i = 0; i < pendaftar.size(); i++) {
                    PenawaranLelang penawaran = pendaftar.get(i);
                    String newStatus = (i < kuota) ? "Menang" : "Kalah";
                    updatePenawaranStatus(penawaran.getId(), newStatus, conn);
                    String notifPesan;
                    if ("Kalah".equals(newStatus)) {
                        userService.updateTotalKredit(penawaran.getMahasiswa().getId(), penawaran.getJumlahKreditDitawar());
                        notifPesan = String.format("😔 Maaf, Anda belum terpilih untuk penempatan di '%s'. Kredit tawaran Anda telah dikembalikan.", lelang.getLokasi());
                    } else {
                        notifPesan = String.format("🏆 SELAMAT! Anda terpilih untuk penempatan di '%s'. Silakan tunggu informasi selanjutnya.", lelang.getLokasi());
                    }
                    notifikasiService.tambahNotifikasi(penawaran.getMahasiswa().getId(), notifPesan);
                }
                updateLelangStatus(lelangId, "Selesai", conn);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Gagal finalisasi lelang, transaksi di-rollback.", e);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Koneksi database gagal saat finalisasi.", e);
            return false;
        }
    }

    public boolean cancelLelang(String lelangId) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Lelang lelang = getLelangById(lelangId).orElseThrow(() -> new SQLException("Lelang tidak ditemukan"));
                if (!"Aktif".equals(lelang.getStatus())) throw new SQLException("Lelang sudah tidak aktif atau telah selesai.");

                // Ambil semua penawaran untuk lelang ini
                List<PenawaranLelang> pendaftar = getPendaftarByLelangId(lelangId);
                for (PenawaranLelang penawaran : pendaftar) {
                    // Kembalikan kredit ke mahasiswa
                    userService.updateTotalKredit(penawaran.getMahasiswa().getId(), penawaran.getJumlahKreditDitawar());
                    // Tambahkan notifikasi
                    notifikasiService.tambahNotifikasi(penawaran.getMahasiswa().getId(),
                            "Lelang di " + lelang.getLokasi() + " telah dibatalkan. Kredit tawaran Anda sebesar " +
                                    penawaran.getJumlahKreditDitawar() + " telah dikembalikan.");
                    // Hapus penawaran
                    updatePenawaranStatus(penawaran.getId(), "Dibatalkan", conn);
                }

                // Ubah status lelang menjadi Dibatalkan
                updateLelangStatus(lelangId, "Dibatalkan", conn);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Gagal membatalkan lelang, transaksi di-rollback.", e);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Koneksi database gagal saat pembatalan lelang.", e);
            return false;
        }
    }
    // </editor-fold>


    // FUNGSI BARU: Untuk mengambil semua pendaftar dari semua lelang aktif
    // Buka file LelangService.java Anda, dan tambahkan metode ini di dalamnya.

    public List<LelangPendaftarInfo> getAllActivePendaftar() {
        List<LelangPendaftarInfo> pendaftarList = new ArrayList<>();
        String sql = "SELECT u.nama, m.id as nim, m.tahun_lulus, l.lokasi, l.kuota, pl.jumlah_kredit_ditawar " +
                "FROM penawaran_lelang pl " +
                "JOIN lelang l ON pl.lelang_id = l.id " +
                "JOIN user u ON pl.mahasiswa_id = u.id " +
                "JOIN mahasiswa m ON u.id = m.id " +
                "WHERE l.status = 'Aktif' " +
                "ORDER BY l.lokasi, pl.jumlah_kredit_ditawar DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pendaftarList.add(new LelangPendaftarInfo(
                        rs.getString("nama"),
                        rs.getString("nim"),
                        rs.getInt("tahun_lulus"),
                        rs.getString("lokasi"),
                        rs.getInt("jumlah_kredit_ditawar"),
                        rs.getInt("kuota")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all active pendaftar lelang", e);
        }
        return pendaftarList;
    }
    // <editor-fold desc="Metode Helper">
    private void updatePenawaranStatus(String penawaranId, String status, Connection conn) throws SQLException {
        String sql = "UPDATE penawaran_lelang SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, penawaranId);
            stmt.executeUpdate();
        }
    }

    private void updateLelangStatus(String lelangId, String status, Connection conn) throws SQLException {
        String sql = "UPDATE lelang SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, lelangId);
            stmt.executeUpdate();
        }
    }

    private Lelang mapRowToLelang(ResultSet rs) throws SQLException {
        return new Lelang(
                rs.getString("id"),
                rs.getString("lokasi"),
                rs.getInt("minimal_kredit"),
                rs.getInt("kuota"),
                rs.getInt("tahun_lelang"),
                rs.getString("deskripsi"),
                rs.getTimestamp("tanggal_selesai") != null ? rs.getTimestamp("tanggal_selesai").toLocalDateTime() : null,
                rs.getString("status")
        );
    }
    // </editor-fold>
}