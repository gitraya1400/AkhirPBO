package com.chrastis.service;

import com.chrastis.data.DatabaseConnection;
import com.chrastis.model.KategoriKredit;
import com.chrastis.model.Kegiatan;
import com.chrastis.model.Kredit;
import com.chrastis.model.SubKategoriKredit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KreditService {

    private static final Logger LOGGER = Logger.getLogger(KreditService.class.getName());
    private final DatabaseConnection dbConnection = new DatabaseConnection();

    public Optional<Kredit> getKreditById(String id) {
        String sql = "SELECT * FROM kredit WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Kredit(
                            rs.getString("id"),
                            rs.getString("jenis"),
                            rs.getString("sub_jenis"),
                            rs.getInt("nilai")
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching kredit by ID: " + id, e);
        }
        return Optional.empty();
    }

    public List<Kegiatan> getAllKegiatan() {
        List<Kegiatan> kegiatanList = new ArrayList<>();
        String sql = "SELECT * FROM kegiatan";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                kegiatanList.add(new Kegiatan(
                        rs.getString("id"),
                        rs.getString("nama"),
                        rs.getString("jenis"),
                        rs.getInt("poin"),
                        rs.getDate("tanggal_akhir") != null ? rs.getDate("tanggal_akhir").toLocalDate() : null
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all kegiatan", e);
        }
        return kegiatanList;
    }

    // ==========================================================
    // == PASTIKAN DUA METODE INI BERADA DI DALAM CLASS INI =====
    // ==========================================================
    public List<KategoriKredit> getAllKategori() {
        List<KategoriKredit> list = new ArrayList<>();
        String sql = "SELECT * FROM kategori_kredit ORDER BY nama_kategori";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new KategoriKredit(rs.getInt("id"), rs.getString("nama_kategori")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all kategori kredit", e);
        }
        return list;
    }

    public List<SubKategoriKredit> getSubKategoriByKategoriId(int kategoriId) {
        List<SubKategoriKredit> list = new ArrayList<>();
        String sql = "SELECT * FROM sub_kategori_kredit WHERE id_kategori = ? ORDER BY nama_sub_kategori";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, kategoriId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new SubKategoriKredit(
                            rs.getInt("id"),
                            rs.getInt("id_kategori"),
                            rs.getString("nama_sub_kategori"),
                            rs.getInt("poin")
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching sub kategori by kategori id", e);
        }
        return list;
    }

} // <-- KURUNG KURAWAL PENUTUP CLASS ADA DI SINI