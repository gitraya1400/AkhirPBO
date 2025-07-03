package com.chrastis.controller;

import com.chrastis.model.Event;
import com.chrastis.service.EventService;
import com.chrastis.util.UIUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EventModalController implements Initializable {

    @FXML private Region overlayBackground;
    @FXML private VBox modalContainer;
    @FXML private Button closeButton;
    @FXML private VBox eventContent;
    @FXML private ImageView currentEventImageView;
    @FXML private Text eventTitleText;
    @FXML private Button prevButton, nextButton;

    private Stage modalStage;
    private int currentImageIndex = 0;

    // Data event dinamis dari database
    private List<Event> activeEvents;
    private final EventService eventService = new EventService();

    public static void showEventModal() {
        try {
            FXMLLoader loader = new FXMLLoader(EventModalController.class.getResource("/fxml/EventModal.fxml"));
            StackPane modalRoot = loader.load();
            EventModalController controller = loader.getController();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initStyle(StageStyle.TRANSPARENT);
            modalStage.setTitle("Event Announcement");
            modalStage.setResizable(false);

            Scene modalScene = new Scene(modalRoot);
            modalScene.setFill(null);
            modalStage.setScene(modalScene);

            controller.setModalStage(modalStage);
            modalStage.centerOnScreen();
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat modal event", e.getMessage());
        }
    }

    public void setModalStage(Stage modalStage) {
        this.modalStage = modalStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Ambil data event yang aktif dari service
        this.activeEvents = eventService.getActiveEvents();

        // 2. Cek apakah ada event, jika tidak tampilkan pesan
        if (activeEvents == null || activeEvents.isEmpty()) {
            eventContent.setVisible(false);
            eventTitleText.setText("Tidak Ada Event");
            return;
        }

        // 3. Tampilkan event pertama
        showEventAtIndex(0);

        // 4. Setup listener
        overlayBackground.setOnMouseClicked(e -> handleClose());
        modalContainer.setOnMouseClicked(e -> e.consume());
    }

    @FXML
    private void handleClose() {
        if (modalStage != null) {
            modalStage.close();
        }
    }

    @FXML
    private void handlePrevImage() {
        currentImageIndex--;
        if (currentImageIndex < 0) {
            currentImageIndex = activeEvents.size() - 1;
        }
        showEventAtIndex(currentImageIndex);
    }

    @FXML
    private void handleNextImage() {
        currentImageIndex++;
        if (currentImageIndex >= activeEvents.size()) {
            currentImageIndex = 0;
        }
        showEventAtIndex(currentImageIndex);
    }

    private void showEventAtIndex(int index) {
        // Dapatkan data event dari list berdasarkan indeks
        Event currentEvent = activeEvents.get(index);

        // Update judul
        eventTitleText.setText(currentEvent.getTitle());

        // Update gambar
        File imageFile = new File(currentEvent.getImagePath());
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString());
            currentEventImageView.setImage(image);
        } else {
            // Tampilkan gambar placeholder jika file tidak ditemukan
            currentEventImageView.setImage(null); // atau gambar default
            System.err.println("File gambar tidak ditemukan di: " + currentEvent.getImagePath());
        }
    }
}