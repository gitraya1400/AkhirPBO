package com.chrastis.service;

import com.chrastis.data.DatabaseConnection;
import com.chrastis.model.TransaksiKopma;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransaksiService {
    private static final Logger LOGGER = Logger.getLogger(TransaksiService.class.getName());
    private final DatabaseConnection dbConnection = new DatabaseConnection();

    // Deklarasi service yang dibutuhkan
    private final NotifikasiService notifikasiService = new NotifikasiService();
    private final KopmaService kopmaService = new KopmaService();

    public List<TransaksiKopma> getAllTransaksi() {
        List<TransaksiKopma> transaksiList = new ArrayList<>();
        String sql = "SELECT * FROM transaksi_kopma ORDER BY tanggal DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                transaksiList.add(mapRowToTransaksi(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all transaksi kopma", e);
        }
        return transaksiList;
    }

    public List<TransaksiKopma> getAllWaitingTransaksi() {
        List<TransaksiKopma> transaksiList = new ArrayList<>();
        String sql = "SELECT * FROM transaksi_kopma WHERE status = 'Menunggu' ORDER BY tanggal DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                transaksiList.add(mapRowToTransaksi(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching waiting transaksi kopma", e);
        }
        return transaksiList;
    }

    public boolean updateTransaksiStatus(int transaksiId, String newStatus) {
        Optional<TransaksiKopma> transaksiOpt = getTransaksiById(transaksiId);
        if (transaksiOpt.isEmpty()) {
            LOGGER.log(Level.WARNING, "Gagal mengirim notifikasi: Transaksi tidak ditemukan untuk ID: " + transaksiId);
            return false;
        }
        TransaksiKopma transaksi = transaksiOpt.get();

        String sql = "UPDATE transaksi_kopma SET status = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, transaksiId);

            if (stmt.executeUpdate() > 0) {
                if ("Selesai".equals(newStatus)) {
                    String namaBarang = kopmaService.getBarangById(transaksi.getBarangId())
                            .map(b -> b.getNama())
                            .orElse("Barang");
                    String pesan = String.format("✅ Transaksi Anda untuk '%s' telah diverifikasi oleh admin.", namaBarang);
                    notifikasiService.tambahNotifikasi(transaksi.getMahasiswaId(), pesan);
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating status for transaksi " + transaksiId, e);
            return false;
        }
    }

    public boolean cancelTransaksi(int transaksiId) {
        String getTransaksiSql = "SELECT * FROM transaksi_kopma WHERE id = ?";
        String getBarangSql = "SELECT harga_kredit, nama FROM barang_kopma WHERE id = ?";
        String kembalikanStokSql = "UPDATE barang_kopma SET stok = stok + 1 WHERE id = ?";
        String kembalikanKreditSql = "UPDATE mahasiswa SET total_kredit = total_kredit + ? WHERE id = ?";
        String batalkanTransaksiSql = "UPDATE transaksi_kopma SET status = 'Dibatalkan' WHERE id = ?";

        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            String mahasiswaId, barangId, namaBarang;
            int hargaKredit;

            try (PreparedStatement getTransaksiStmt = conn.prepareStatement(getTransaksiSql)) {
                getTransaksiStmt.setInt(1, transaksiId);
                ResultSet rs = getTransaksiStmt.executeQuery();
                if (!rs.next()) { conn.rollback(); return false; }
                mahasiswaId = rs.getString("mahasiswa_id");
                barangId = rs.getString("barang_id");
            }

            try (PreparedStatement getBarangStmt = conn.prepareStatement(getBarangSql)) {
                getBarangStmt.setString(1, barangId);
                ResultSet rs = getBarangStmt.executeQuery();
                if (!rs.next()) { conn.rollback(); return false; }
                hargaKredit = rs.getInt("harga_kredit");
                namaBarang = rs.getString("nama");
            }

            try (PreparedStatement kembalikanStokStmt = conn.prepareStatement(kembalikanStokSql)) {
                kembalikanStokStmt.setString(1, barangId);
                kembalikanStokStmt.executeUpdate();
            }
            try (PreparedStatement kembalikanKreditStmt = conn.prepareStatement(kembalikanKreditSql)) {
                kembalikanKreditStmt.setInt(1, hargaKredit);
                kembalikanKreditStmt.setString(2, mahasiswaId);
                kembalikanKreditStmt.executeUpdate();
            }

            try (PreparedStatement batalkanTransaksiStmt = conn.prepareStatement(batalkanTransaksiSql)) {
                batalkanTransaksiStmt.setInt(1, transaksiId);
                batalkanTransaksiStmt.executeUpdate();
            }

            conn.commit();

            String pesan = String.format("🔄 Transaksi untuk '%s' dibatalkan oleh Admin. Kredit Anda sebesar %d poin telah dikembalikan.", namaBarang, hargaKredit);
            notifikasiService.tambahNotifikasi(mahasiswaId, pesan);

            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { LOGGER.log(Level.SEVERE, "Rollback failed", ex); }
            LOGGER.log(Level.SEVERE, "Error cancelling transaction", e);
            return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Failed to close connection", e); }
        }
    }

    public Optional<TransaksiKopma> getTransaksiById(int transaksiId) {
        String sql = "SELECT * FROM transaksi_kopma WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transaksiId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToTransaksi(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching transaksi by ID", e);
        }
        return Optional.empty();
    }

    private TransaksiKopma mapRowToTransaksi(ResultSet rs) throws SQLException {
        return new TransaksiKopma(
                rs.getInt("id"),
                rs.getString("mahasiswa_id"),
                rs.getString("barang_id"),
                rs.getString("kode_penukaran"),
                rs.getDate("tanggal"),
                rs.getString("status")
        );
    }
}