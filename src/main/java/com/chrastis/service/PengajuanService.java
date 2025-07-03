package com.chrastis.service;

import com.chrastis.data.DatabaseConnection;
import com.chrastis.model.Mahasiswa;
import com.chrastis.model.PengajuanKredit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PengajuanService {
    private static final Logger LOGGER = Logger.getLogger(PengajuanService.class.getName());
    private final DatabaseConnection dbConnection = new DatabaseConnection();
    private final NotifikasiService notifikasiService = new NotifikasiService();

    public boolean approvePengajuan(PengajuanKredit pengajuan) {
        String updatePengajuanSql = "UPDATE pengajuan_kredit SET status = 'Disetujui', keterangan = NULL WHERE id = ?";
        String updateUserKreditSql = "UPDATE mahasiswa SET total_kredit = total_kredit + ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtPengajuan = conn.prepareStatement(updatePengajuanSql);
                 PreparedStatement stmtUser = conn.prepareStatement(updateUserKreditSql)) {
                stmtPengajuan.setString(1, pengajuan.getId());
                stmtPengajuan.executeUpdate();
                stmtUser.setInt(1, pengajuan.getNilai());
                stmtUser.setString(2, pengajuan.getMahasiswa().getId());
                stmtUser.executeUpdate();
                conn.commit();

                // === PESAN NOTIFIKASI YANG LEBIH BAIK ===
                String deskripsiSingkat = pengajuan.getDeskripsi().length() > 30 ? pengajuan.getDeskripsi().substring(0, 30) + "..." : pengajuan.getSubKategori();
                String pesan = String.format("✅ Selamat! Pengajuan untuk '%s' (+%d poin) telah disetujui.", deskripsiSingkat, pengajuan.getNilai());
                notifikasiService.tambahNotifikasi(pengajuan.getMahasiswa().getId(), pesan);

                return true;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Gagal menyetujui, transaksi di-rollback", e);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Koneksi database gagal saat approval", e);
            return false;
        }
    }

    public boolean rejectPengajuan(String pengajuanId, String mahasiswaId, String alasan) {
        String sql = "UPDATE pengajuan_kredit SET status = 'Ditolak', keterangan = ? WHERE id = ?";
        try(Connection conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, alasan);
            stmt.setString(2, pengajuanId);
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                // === PESAN NOTIFIKASI YANG LEBIH BAIK ===
                getPengajuanById(pengajuanId).ifPresent(pengajuan -> {
                    String deskripsiSingkat = pengajuan.getDeskripsi().length() > 30 ? pengajuan.getDeskripsi().substring(0, 30) + "..." : pengajuan.getSubKategori();
                    String pesan = String.format("❌ Maaf, pengajuan untuk '%s' ditolak. Alasan: %s", deskripsiSingkat, alasan);
                    notifikasiService.tambahNotifikasi(mahasiswaId, pesan);
                });
            }
            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Gagal menolak pengajuan", e);
            return false;
        }
    }

    // Metode lain di bawah ini tidak perlu diubah

    public List<PengajuanKredit> getAllPengajuan() {
        List<PengajuanKredit> list = new ArrayList<>();
        String sql = "SELECT p.*, u.nama as mahasiswa_nama, m.ipk, m.tahun_lulus, m.total_kredit FROM pengajuan_kredit p JOIN user u ON p.mahasiswa_id = u.id JOIN mahasiswa m ON u.id = m.id ORDER BY p.tanggal DESC";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) { list.add(mapRowToPengajuan(rs)); }
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Error fetching all pengajuan", e); }
        return list;
    }

    public List<PengajuanKredit> getPendingPengajuan() {
        List<PengajuanKredit> list = new ArrayList<>();
        String sql = "SELECT p.*, u.nama as mahasiswa_nama, m.ipk, m.tahun_lulus, m.total_kredit FROM pengajuan_kredit p JOIN user u ON p.mahasiswa_id = u.id JOIN mahasiswa m ON u.id = m.id WHERE p.status = 'Menunggu' ORDER BY p.tanggal DESC";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) { list.add(mapRowToPengajuan(rs)); }
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Error fetching pending pengajuan", e); }
        return list;
    }

    public List<PengajuanKredit> getPengajuanByMahasiswa(String mahasiswaId) {
        List<PengajuanKredit> pengajuanList = new ArrayList<>();
        String sql = "SELECT p.*, u.nama as mahasiswa_nama, m.ipk, m.tahun_lulus, m.total_kredit FROM pengajuan_kredit p JOIN user u ON p.mahasiswa_id = u.id JOIN mahasiswa m ON u.id = m.id WHERE p.mahasiswa_id = ? ORDER BY p.tanggal DESC";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mahasiswaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { pengajuanList.add(mapRowToPengajuan(rs)); }
            }
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Error fetching pengajuan for " + mahasiswaId, e); }
        return pengajuanList;
    }

    public Optional<PengajuanKredit> getPengajuanById(String pengajuanId) {
        String sql = "SELECT p.*, u.nama as mahasiswa_nama, m.ipk, m.tahun_lulus, m.total_kredit FROM pengajuan_kredit p JOIN user u ON p.mahasiswa_id = u.id JOIN mahasiswa m ON u.id = m.id WHERE p.id = ?";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pengajuanId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { return Optional.of(mapRowToPengajuan(rs)); }
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Error fetching pengajuan by ID", e); }
        return Optional.empty();
    }

    public boolean tambahPengajuan(PengajuanKredit pengajuan) {
        String sql = "INSERT INTO pengajuan_kredit (id, mahasiswa_id, id_sub_kategori, bukti, tanggal, status, nilai, kategori, sub_kategori, deskripsi, keterangan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pengajuan.getId());
            stmt.setString(2, pengajuan.getMahasiswa().getId());
            stmt.setString(3, pengajuan.getKreditId());
            stmt.setString(4, pengajuan.getBukti());
            stmt.setDate(5, java.sql.Date.valueOf(pengajuan.getTanggal()));
            stmt.setString(6, pengajuan.getStatus());
            stmt.setInt(7, pengajuan.getNilai());
            stmt.setString(8, pengajuan.getKategori());
            stmt.setString(9, pengajuan.getSubKategori());
            stmt.setString(10, pengajuan.getDeskripsi());
            stmt.setString(11, pengajuan.getKeterangan());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Gagal menambah pengajuan baru", e);
            e.printStackTrace();
            return false;
        }
    }

    private PengajuanKredit mapRowToPengajuan(ResultSet rs) throws SQLException {
        Mahasiswa m = new Mahasiswa(
                rs.getString("mahasiswa_id"),
                rs.getString("mahasiswa_nama"),
                rs.getDouble("ipk"),
                rs.getInt("tahun_lulus"),
                rs.getInt("total_kredit")
        );
        PengajuanKredit p = new PengajuanKredit(
                rs.getString("id"), m, rs.getString("id_sub_kategori"),
                rs.getString("bukti"), rs.getDate("tanggal").toLocalDate()
        );
        p.setStatus(rs.getString("status"));
        p.setNilai(rs.getInt("nilai"));
        p.setKeterangan(rs.getString("keterangan"));
        p.setKategori(rs.getString("kategori"));
        p.setSubKategori(rs.getString("sub_kategori"));
        p.setDeskripsi(rs.getString("deskripsi"));
        return p;
    }
}