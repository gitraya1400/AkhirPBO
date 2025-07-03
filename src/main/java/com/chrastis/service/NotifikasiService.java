package com.chrastis.service;

import com.chrastis.data.DatabaseConnection;
import com.chrastis.model.Notifikasi;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotifikasiService {
    private static final Logger LOGGER = Logger.getLogger(NotifikasiService.class.getName());
    private final DatabaseConnection dbConnection = new DatabaseConnection();

    public void tambahNotifikasi(String mahasiswaId, String pesan) {
        String sql = "INSERT INTO notifikasi (id, mahasiswa_id, pesan, tanggal, status) VALUES (?, ?, ?, ?, 'Belum Dibaca')";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, mahasiswaId);
            stmt.setString(3, pesan);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding notification for " + mahasiswaId, e);
        }
    }



    public List<Notifikasi> getNotifikasiBelumDibaca(String mahasiswaId) {
        List<Notifikasi> notifikasiList = new ArrayList<>();
        String sql = "SELECT * FROM notifikasi WHERE mahasiswa_id = ? AND status = 'Belum Dibaca' ORDER BY tanggal DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mahasiswaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifikasiList.add(new Notifikasi(
                            rs.getString("id"),
                            rs.getString("mahasiswa_id"),
                            rs.getString("pesan"),
                            rs.getTimestamp("tanggal").toLocalDateTime(),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching unread notifications for " + mahasiswaId, e);
        }
        return notifikasiList;
    }

    public void tandaiNotifikasiDibaca(String notifikasiId) {
        String sql = "UPDATE notifikasi SET status = 'Dibaca' WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notifikasiId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking notification as read " + notifikasiId, e);
        }
    }

    public List<Notifikasi> getAllNotifikasiByMahasiswa(String mahasiswaId) {
        List<Notifikasi> notifikasiList = new ArrayList<>();
        // Mengurutkan berdasarkan tanggal terbaru, lalu status (Belum Dibaca di atas)
        String sql = "SELECT * FROM notifikasi WHERE mahasiswa_id = ? ORDER BY tanggal DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mahasiswaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifikasiList.add(new Notifikasi(
                            rs.getString("id"),
                            rs.getString("mahasiswa_id"),
                            rs.getString("pesan"),
                            rs.getTimestamp("tanggal").toLocalDateTime(),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all notifications for " + mahasiswaId, e);
        }
        return notifikasiList;
    }
}