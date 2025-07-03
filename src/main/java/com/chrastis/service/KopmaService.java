package com.chrastis.service;

import com.chrastis.data.DatabaseConnection;
import com.chrastis.model.BarangKopma;
import com.chrastis.model.TransaksiKopma;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KopmaService {
    private static final Logger LOGGER = Logger.getLogger(KopmaService.class.getName());
    private final DatabaseConnection dbConnection = new DatabaseConnection();

    //<editor-fold desc="Metode GET (Mengambil Data)">
    public Optional<BarangKopma> getBarangById(String barangId) {
        String sql = "SELECT * FROM barang_kopma WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, barangId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToBarang(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching barang kopma by ID: " + barangId, e);
        }
        return Optional.empty();
    }

    public List<BarangKopma> getAllBarangKopma() {
        List<BarangKopma> barangList = new ArrayList<>();
        String sql = "SELECT * FROM barang_kopma ORDER BY kategori, nama";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                barangList.add(mapRowToBarang(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all barang kopma", e);
        }
        return barangList;
    }

    public List<TransaksiKopma> getTransaksiByMahasiswa(String mahasiswaId) {
        List<TransaksiKopma> transaksiList = new ArrayList<>();
        String sql = "SELECT * FROM transaksi_kopma WHERE mahasiswa_id = ? ORDER BY tanggal DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mahasiswaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transaksiList.add(new TransaksiKopma(
                            rs.getInt("id"), rs.getString("mahasiswa_id"), rs.getString("barang_id"),
                            rs.getString("kode_penukaran"), rs.getDate("tanggal"), rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching transaksi for " + mahasiswaId, e);
        }
        return transaksiList;
    }
    //</editor-fold>

    //<editor-fold desc="Metode CUD (Create, Update, Delete) untuk Admin">
    public boolean createBarang(BarangKopma barang) {
        String sql = "INSERT INTO barang_kopma (id, nama, harga_kredit, stok, deskripsi, kategori) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, barang.getId());
            stmt.setString(2, barang.getNama());
            stmt.setInt(3, barang.getHargaKredit());
            stmt.setInt(4, barang.getStok());
            stmt.setString(5, barang.getDeskripsi());
            stmt.setString(6, barang.getKategori());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating barang", e);
            return false;
        }
    }

    public boolean updateBarang(BarangKopma barang) {
        String sql = "UPDATE barang_kopma SET nama = ?, harga_kredit = ?, stok = ?, deskripsi = ?, kategori = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, barang.getNama());
            stmt.setInt(2, barang.getHargaKredit());
            stmt.setInt(3, barang.getStok());
            stmt.setString(4, barang.getDeskripsi());
            stmt.setString(5, barang.getKategori());
            stmt.setString(6, barang.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating barang", e);
            return false;
        }
    }

    public boolean deleteBarang(String barangId) {
        String sql = "DELETE FROM barang_kopma WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, barangId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting barang. It might be referenced in transaction history.", e);
            return false;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Logika Transaksi Mahasiswa">
    public boolean tukarBarang(String mahasiswaId, String barangId, int hargaKredit) {
        String checkStokSql = "SELECT stok FROM barang_kopma WHERE id = ? FOR UPDATE";
        String updateStokSql = "UPDATE barang_kopma SET stok = stok - 1 WHERE id = ?";
        String updateKreditSql = "UPDATE mahasiswa SET total_kredit = total_kredit - ? WHERE id = ?";
        String insertTransaksiSql = "INSERT INTO transaksi_kopma (mahasiswa_id, barang_id, kode_penukaran, tanggal, status) VALUES (?, ?, ?, ?, 'Menunggu')";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement checkStmt = conn.prepareStatement(checkStokSql);
                 PreparedStatement updateStokStmt = conn.prepareStatement(updateStokSql);
                 PreparedStatement updateKreditStmt = conn.prepareStatement(updateKreditSql);
                 PreparedStatement insertTransaksiStmt = conn.prepareStatement(insertTransaksiSql)) {
                checkStmt.setString(1, barangId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt("stok") > 0) {
                    updateStokStmt.setString(1, barangId);
                    updateStokStmt.executeUpdate();
                    updateKreditStmt.setInt(1, hargaKredit);
                    updateKreditStmt.setString(2, mahasiswaId);
                    updateKreditStmt.executeUpdate();
                    insertTransaksiStmt.setString(1, mahasiswaId);
                    insertTransaksiStmt.setString(2, barangId);
                    insertTransaksiStmt.setString(3, generateKodePenukaran());
                    insertTransaksiStmt.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
                    insertTransaksiStmt.executeUpdate();
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Error during transaction, rolled back", e);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error", e);
            return false;
        }
    }

    private String generateKodePenukaran() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder kode = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 8; i++) {
            kode.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return kode.toString();
    }
    //</editor-fold>

    private BarangKopma mapRowToBarang(ResultSet rs) throws SQLException {
        return new BarangKopma(
                rs.getString("id"),
                rs.getString("nama"),
                rs.getInt("harga_kredit"),
                rs.getInt("stok"),
                rs.getString("deskripsi"),
                rs.getString("kategori")
        );
    }
}