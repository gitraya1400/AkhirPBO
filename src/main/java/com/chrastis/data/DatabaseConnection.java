package com.chrastis.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chrastis_db_final?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Gagal membuat koneksi database.", e);
            throw new RuntimeException("Tidak dapat terhubung ke database.", e);
        }
    }
}