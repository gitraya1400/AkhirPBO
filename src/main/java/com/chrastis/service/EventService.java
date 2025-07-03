package com.chrastis.service;

import com.chrastis.data.DatabaseConnection;
import com.chrastis.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventService {
    private static final Logger LOGGER = Logger.getLogger(EventService.class.getName());
    private final DatabaseConnection dbConnection = new DatabaseConnection();

    // Mengambil event yang masih aktif untuk mahasiswa
    public List<Event> getActiveEvents() {
        List<Event> eventList = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status = 'Aktif' AND deadline > NOW() ORDER BY event_date ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                eventList.add(mapRowToEvent(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching active events", e);
        }
        return eventList;
    }

    // FUNGSI BARU: Mengambil SEMUA event untuk admin
    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY event_date DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                eventList.add(mapRowToEvent(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all events", e);
        }
        return eventList;
    }

    // FUNGSI BARU: Menambah event baru
    public boolean createEvent(Event event) {
        String sql = "INSERT INTO events (id, title, description, image_path, event_date, deadline, location, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getId());
            stmt.setString(2, event.getTitle());
            stmt.setString(3, event.getDescription());
            stmt.setString(4, event.getImagePath());
            stmt.setTimestamp(5, Timestamp.valueOf(event.getEventDate()));
            stmt.setTimestamp(6, Timestamp.valueOf(event.getDeadline()));
            stmt.setString(7, event.getLocation());
            stmt.setString(8, event.getStatus());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating event", e);
            return false;
        }
    }

    // FUNGSI BARU: Mengedit event
    public boolean updateEvent(Event event) {
        String sql = "UPDATE events SET title = ?, description = ?, image_path = ?, event_date = ?, deadline = ?, location = ?, status = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getImagePath());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getEventDate()));
            stmt.setTimestamp(5, Timestamp.valueOf(event.getDeadline()));
            stmt.setString(6, event.getLocation());
            stmt.setString(7, event.getStatus());
            stmt.setString(8, event.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating event", e);
            return false;
        }
    }

    // FUNGSI BARU: Menghapus event
    public boolean deleteEvent(String eventId) {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting event", e);
            return false;
        }
    }

    private Event mapRowToEvent(ResultSet rs) throws SQLException {
        return new Event(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("image_path"),
                rs.getTimestamp("event_date") != null ? rs.getTimestamp("event_date").toLocalDateTime() : null,
                rs.getTimestamp("deadline").toLocalDateTime(),
                rs.getString("location"),
                rs.getString("status")
        );
    }
}