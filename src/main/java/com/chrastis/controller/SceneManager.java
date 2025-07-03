package com.chrastis.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(Scene scene) {
        if (primaryStage != null) {
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
