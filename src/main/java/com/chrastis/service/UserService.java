package com.chrastis.service;

import com.chrastis.data.DatabaseConnection;
import com.chrastis.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {

    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private final DatabaseConnection dbConnection = new DatabaseConnection();

    public Optional<User> validateUser(String id, String password) {
        String sql = "SELECT * FROM user WHERE id = ? AND password = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("User found in database: " + id + " with role: " + rs.getString("role"));
                    return getUserById(id); // Ambil detail user jika password cocok
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error validating user", e);
        }
        System.out.println("No user found for credentials: " + id);
        return Optional.empty();
    }

    public Optional<User> getUserById(String id) {
        String sql = "SELECT u.id, u.nama, u.role, m.ipk, m.tahun_lulus, m.total_kredit " +
                "FROM user u LEFT JOIN mahasiswa m ON u.id = m.id WHERE u.id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    String nama = rs.getString("nama");

                    System.out.println("Creating user - ID: " + id + ", Name: " + nama + ", Role: " + role);

                    switch (role) {
                        case "Mahasiswa":
                            // Handle case where tahun_lulus might be null
                            int tahunLulus = rs.getInt("tahun_lulus");
                            if (rs.wasNull()) {
                                tahunLulus = 2025; // Default value
                            }
                            System.out.println("Created Mahasiswa: " + nama);
                            return Optional.of(new Mahasiswa(id, nama, rs.getDouble("ipk"), tahunLulus, rs.getInt("total_kredit")));

                        case "AdminKopma":
                            // Direct AdminKopma dari database
                            System.out.println("Created AdminKopma: " + nama);
                            return Optional.of(new AdminKopma(id, nama));

                        case "AdminKredit":
                            // Direct AdminKredit dari database
                            System.out.println("Created AdminKredit: " + nama);
                            return Optional.of(new AdminKredit(id, nama));

                        case "Admin":
                            // Legacy support - determine admin type based on ID
                            if (id.toLowerCase().contains("kopma") || nama.toLowerCase().contains("kopma")) {
                                System.out.println("Created AdminKopma (from generic Admin): " + nama);
                                return Optional.of(new AdminKopma(id, nama));
                            } else {
                                System.out.println("Created AdminKredit (from generic Admin): " + nama);
                                return Optional.of(new AdminKredit(id, nama));
                            }

                        default:
                            LOGGER.warning("Unknown role: " + role + " for user: " + id);
                            System.out.println("Unknown role: " + role + " for user: " + id);
                            return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching user by ID: " + id, e);
        }
        System.out.println("No user found in database for ID: " + id);
        return Optional.empty();
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.id, u.nama, u.role, m.ipk, m.total_kredit " +
                "FROM user u LEFT JOIN mahasiswa m ON u.id = m.id";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                getUserById(rs.getString("id")).ifPresent(users::add);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all users", e);
        }
        return users;
    }

    public boolean updateTotalKredit(String mahasiswaId, int poin) {
        String sql = "UPDATE mahasiswa SET total_kredit = total_kredit + ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, poin);
            stmt.setString(2, mahasiswaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating total kredit for " + mahasiswaId, e);
            return false;
        }
    }
}