package com.chrastis.controller;

import com.chrastis.model.AdminKopma;
import com.chrastis.model.AdminKredit;
import com.chrastis.model.Mahasiswa;
import com.chrastis.model.User;
import com.chrastis.service.UserService;
import com.chrastis.util.SceneManager;
import com.chrastis.util.UIUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField nimField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        // Enter key handlers
        passwordField.setOnAction(this::handleLogin);
        nimField.setOnAction(e -> passwordField.requestFocus());

        // Disable login button initially (optional UX enhancement)
        updateLoginButtonState();

        // Add listeners to enable/disable login button based on input
        nimField.textProperty().addListener((obs, oldText, newText) -> updateLoginButtonState());
        passwordField.textProperty().addListener((obs, oldText, newText) -> updateLoginButtonState());

        System.out.println("LoginController initialized");
    }

    /**
     * Update login button state based on input fields
     */
    private void updateLoginButtonState() {
        boolean hasInput = !nimField.getText().trim().isEmpty() && !passwordField.getText().isEmpty();
        loginButton.setDisable(!hasInput);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String id = nimField.getText().trim();
        String password = passwordField.getText();

        System.out.println("Login attempt - ID: " + id);

        if (id.isEmpty() || password.isEmpty()) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", null, "NIM/ID dan password tidak boleh kosong.");
            return;
        }

        // Disable login button during authentication
        loginButton.setDisable(true);
        loginButton.setText("Logging in...");

        // Validate user credentials
        userService.validateUser(id, password).ifPresentOrElse(
                user -> {
                    System.out.println("Login successful for user: " + user.getId() + " (" + user.getClass().getSimpleName() + ")");
                    navigateToDashboard(user);
                },
                () -> {
                    // Re-enable login button on failure
                    loginButton.setDisable(false);
                    loginButton.setText("LOGIN");

                    System.out.println("Login failed for ID: " + id);
                    UIUtils.showAlert(Alert.AlertType.ERROR, "Login Gagal", null,
                            "NIM/ID atau password yang Anda masukkan salah.\n\nAdmin KOPMA: kopma1 / kopma123\nAdmin Kredit: kredit1 / kredit123");
                    passwordField.clear();
                    nimField.requestFocus();
                }
        );
    }

    /**
     * Navigate to dashboard - ALL USERS use the same dashboard.fxml
     */
    private void navigateToDashboard(User user) {
        try {
            System.out.println("Navigating to dashboard for user type: " + user.getClass().getSimpleName());

            // ALL users (admin and students) use the same dashboard.fxml
            // DashboardController will handle admin vs student logic
            SceneManager.navigateTo("/fxml/dashboard.fxml", 1200, 800, (DashboardController controller) -> {
                System.out.println("Setting user in DashboardController: " + user.getNama() + " (" + user.getClass().getSimpleName() + ")");
                controller.setUser(user);
                controller.initializeData();

                System.out.println("Dashboard loaded successfully for: " + user.getNama());
            });

            // Show appropriate welcome message based on user type
            Platform.runLater(() -> {
                try {
                    Thread.sleep(500); // Small delay to ensure dashboard is fully loaded

                    if (user instanceof AdminKredit || user instanceof AdminKopma) {
                        // Admin welcome message
                        String adminType = user instanceof AdminKredit ? "Kredit & Lelang" : "KOPMA";
                        UIUtils.showAlert(Alert.AlertType.INFORMATION, "Selamat Datang Administrator!",
                                "Login Admin Berhasil",
                                "Selamat datang, " + user.getNama() + "!\n\n" +
                                        "Anda login sebagai Admin " + adminType + "\n\n" +
                                        "🔧 Klik tombol 'Admin Panel' di sidebar untuk mengakses fitur administrasi.\n\n" +
                                        "Dashboard ini menampilkan overview sistem dengan akses admin penuh.");
                    } else if (user instanceof Mahasiswa) {
                        // Student - show event modal as usual
                        try {
                            EventModalController.showEventModal();
                            System.out.println("Event modal displayed for student: " + user.getId());
                        } catch (Exception e) {
                            System.err.println("Error showing event modal: " + e.getMessage());
                        }
                    }

                } catch (InterruptedException e) {
                    System.err.println("Interrupted while showing welcome message: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Error showing welcome message: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            // Re-enable login button on navigation failure
            loginButton.setDisable(false);
            loginButton.setText("LOGIN");

            System.err.println("Error navigating to dashboard: " + e.getMessage());
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Error", "Navigation Error",
                    "Terjadi kesalahan saat membuka dashboard: " + e.getMessage());
        }
    }
}