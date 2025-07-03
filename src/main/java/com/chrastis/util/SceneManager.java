package com.chrastis.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class SceneManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void switchScene(Scene scene) {
        if (primaryStage != null) {
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        }
    }

    public static <T> void navigateTo(String fxmlPath, int width, int height, Consumer<T> controllerInitializer) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), width, height);
            scene.getStylesheets().add(SceneManager.class.getResource("/css/styles.css").toExternalForm());

            if (controllerInitializer != null) {
                controllerInitializer.accept(loader.getController());
            }

            switchScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            UIUtils.showAlert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Navigasi Gagal",
                    "Tidak dapat memuat halaman.",
                    "Terjadi kesalahan saat mencoba membuka: " + fxmlPath
            );
        }
    }
}