package com.chrastis.controller;

import com.chrastis.util.SceneManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.image.ImageView;

import java.util.Arrays;
import java.util.List;

public class LoadingController {

    @FXML private ProgressBar loadingProgress;
    @FXML private Label loadingPercentage;
    @FXML private Text loadingStatusText;
    @FXML private Text securityStatusText;
    @FXML private Text experienceStatusText;
    @FXML private ImageView appLogo; // Logo gambar

    // Main titles
    @FXML private Text chrastisTitle;
    @FXML private Text systemSubtitle;

    // FXML fields untuk setiap huruf STAT CREDIT
    @FXML private Text statChar1;
    @FXML private Text statChar2;
    @FXML private Text statChar3;
    @FXML private Text statChar4;
    @FXML private Text creditChar1;
    @FXML private Text creditChar2;
    @FXML private Text creditChar3;
    @FXML private Text creditChar4;
    @FXML private Text creditChar5;
    @FXML private Text creditChar6;

    private List<Text> statCreditChars;

    @FXML
    public void initialize() {
        // Inisialisasi daftar karakter setelah FXML dimuat
        statCreditChars = Arrays.asList(
                statChar1, statChar2, statChar3, statChar4,
                creditChar1, creditChar2, creditChar3, creditChar4, creditChar5, creditChar6
        );

        // Debug: Cek apakah semua field sudah ter-inject
        System.out.println("=== DEBUG INJECTION ===");
        System.out.println("appLogo: " + (appLogo != null ? "OK" : "NULL"));
        System.out.println("chrastisTitle: " + (chrastisTitle != null ? "OK" : "NULL"));
        System.out.println("statChar1: " + (statChar1 != null ? "OK" : "NULL"));
        System.out.println("loadingProgress: " + (loadingProgress != null ? "OK" : "NULL"));

        // Mulai animasi intro lengkap
        startIntroAnimation();
    }

    private void startIntroAnimation() {
        SequentialTransition mainSequence = new SequentialTransition();

        // 1. Animasi Logo Image (0.6 detik)
        FadeTransition logoAnimation = new FadeTransition(Duration.millis(600), appLogo);
        if (appLogo != null) {
            logoAnimation.setFromValue(0.0);
            logoAnimation.setToValue(1.0);
        }

        // 2. Animasi STAT CREDIT per huruf (1 detik)
        SequentialTransition charAnimation = new SequentialTransition();
        for (Text character : statCreditChars) {
            if (character != null) {
                FadeTransition ft = new FadeTransition(Duration.millis(80), character);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                charAnimation.getChildren().add(ft);
                // Tambahkan jeda kecil antar huruf untuk efek ketikan
                charAnimation.getChildren().add(new PauseTransition(Duration.millis(40)));
            }
        }

        // 3. Animasi CHRASTIS title dan subtitle (0.8 detik)
        ParallelTransition titleAnimation = new ParallelTransition();
        if (chrastisTitle != null) {
            FadeTransition chrastis = new FadeTransition(Duration.millis(600), chrastisTitle);
            chrastis.setFromValue(0.0);
            chrastis.setToValue(1.0);
            titleAnimation.getChildren().add(chrastis);
        }
        if (systemSubtitle != null) {
            FadeTransition subtitle = new FadeTransition(Duration.millis(600), systemSubtitle);
            subtitle.setFromValue(0.0);
            subtitle.setToValue(1.0);
            subtitle.setDelay(Duration.millis(200));
            titleAnimation.getChildren().add(subtitle);
        }

        // Susun semua animasi secara berurutan
        if (appLogo != null) {
            mainSequence.getChildren().add(logoAnimation);
            mainSequence.getChildren().add(new PauseTransition(Duration.millis(200)));
        }
        mainSequence.getChildren().add(charAnimation);
        mainSequence.getChildren().add(new PauseTransition(Duration.millis(300)));
        mainSequence.getChildren().add(titleAnimation);

        // Setelah animasi intro selesai, mulai loading
        mainSequence.setOnFinished(e -> {
            PauseTransition delay = new PauseTransition(Duration.millis(800));
            delay.setOnFinished(event -> simulateLoading());
            delay.play();
        });

        mainSequence.play();
        System.out.println("Animasi intro lengkap dimulai");
    }

    private void simulateLoading() {
        Thread loadingThread = new Thread(() -> {
            try {
                // Tahap 1: Memuat aplikasi (0% - 30%)
                updateProgress(0.0, "Memuat aplikasi...", 0.3);
                Thread.sleep(1500);

                // Tahap 2: Mengatur keamanan (30% - 60%)
                updateProgress(0.3, "Mengatur keamanan...", 0.6);
                Platform.runLater(() -> {
                    if (securityStatusText != null) {
                        FadeTransition ft = new FadeTransition(Duration.millis(300), securityStatusText);
                        ft.setFromValue(0.3);
                        ft.setToValue(1.0);
                        ft.play();
                    }
                });
                Thread.sleep(1500);

                // Tahap 3: Menyiapkan pengalaman terbaik (60% - 100%)
                updateProgress(0.6, "Menyiapkan pengalaman terbaik untuk Anda", 1.0);
                Platform.runLater(() -> {
                    if (experienceStatusText != null) {
                        FadeTransition ft = new FadeTransition(Duration.millis(300), experienceStatusText);
                        ft.setFromValue(0.3);
                        ft.setToValue(1.0);
                        ft.play();
                    }
                });
                Thread.sleep(1500);

                // Selesai loading, navigasi ke halaman login
                Platform.runLater(() -> {
                    SceneManager.navigateTo("/fxml/login.fxml", 550, 650, null);
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    private void updateProgress(double startProgress, String statusText, double endProgress) {
        final double totalSteps = 100;
        for (int i = 0; i <= totalSteps; i++) {
            double currentProgress = startProgress + (endProgress - startProgress) * (i / totalSteps);
            int percentage = (int) (currentProgress * 100);
            final int finalPercentage = percentage;
            final double finalProgress = currentProgress;

            Platform.runLater(() -> {
                if (loadingProgress != null) {
                    loadingProgress.setProgress(finalProgress);
                }
                if (loadingPercentage != null) {
                    loadingPercentage.setText(finalPercentage + "%");
                }
                if (loadingStatusText != null) {
                    loadingStatusText.setText(statusText);
                }
            });

            try {
                Thread.sleep(8); // Sedikit lebih lambat untuk efek yang smooth
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}