package com.chrastis.controller;

import com.chrastis.model.AdminKopma;
import com.chrastis.model.AdminKredit;
import com.chrastis.model.User;
import com.chrastis.util.SceneManager;
import com.chrastis.util.UIUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML private Label welcomeNavText;
    @FXML private Button logoutButton;
    @FXML private Button adminButton;
    @FXML private VBox mainContentScroll;

    private User user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("AdminDashboardController initialized");
    }

    public void setUser(User user) {
        this.user = user;
        System.out.println("Admin user set: " + (user != null ? user.getNama() + " (" + user.getClass().getSimpleName() + ")" : "null"));
        initializeData();
    }

    public void initializeData() { // Pastikan ini public
        if (user == null) {
            System.out.println("User is null, skipping initialization");
            return;
        }

        welcomeNavText.setText("Dashboard Administrator");
        String role = (user instanceof AdminKopma) ? "Administrator KOPMA" : (user instanceof AdminKredit) ? "Administrator Kredit" : "Administrator Tidak Diketahui";
        Label welcomeLabel = new Label("Dashboard Administrator");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label subtitleLabel = new Label("Gunakan Admin Panel untuk mengelola sistem");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6c757d;");
        mainContentScroll.getChildren().setAll(welcomeLabel, subtitleLabel);

        System.out.println("Admin dashboard initialized for: " + user.getNama() + " (" + role + ")");
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logout clicked by: " + (user != null ? user.getClass().getSimpleName() : "Unknown"));
        SceneManager.navigateTo("/fxml/login.fxml", 550, 650, null);
    }

    @FXML
    private void handleAdminPanel() {
        System.out.println("Admin Panel accessed by: " + user.getClass().getSimpleName());
        SceneManager.navigateTo("/fxml/admin.fxml", 1400, 900, (AdminController c) -> {
            c.setUser(user);
            c.initializeData();
        });
    }
}