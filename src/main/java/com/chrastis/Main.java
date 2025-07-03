package com.chrastis;

import com.chrastis.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.setPrimaryStage(primaryStage);
        SceneManager.navigateTo("/fxml/loading.fxml", 800, 600, null);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("STAT CREDIT");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}