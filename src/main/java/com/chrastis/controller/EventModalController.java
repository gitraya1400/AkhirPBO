package com.chrastis.controller;

import com.chrastis.model.Event;
import com.chrastis.service.EventService;
import com.chrastis.util.UIUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
        System.out.println("=== EventModalController Initialize START ===");

        try {
            // 1. Ambil data event yang aktif dari service
            this.activeEvents = eventService.getActiveEvents();
            System.out.println("Events loaded from service: " + (activeEvents != null ? activeEvents.size() : "null"));

            // Debug: Print semua event yang didapat
            if (activeEvents != null && !activeEvents.isEmpty()) {
                for (int i = 0; i < activeEvents.size(); i++) {
                    Event event = activeEvents.get(i);
                    System.out.println("Event " + i + ": " + event.getTitle() + " | Image: " + event.getImagePath());
                }
            }

            // 2. Cek apakah ada event
            if (activeEvents == null || activeEvents.isEmpty()) {
                System.out.println("No active events found - hiding navigation");
                eventContent.setVisible(false);
                eventTitleText.setText("Tidak Ada Event");
                prevButton.setVisible(false);
                nextButton.setVisible(false);
                return;
            }

            // Reset current index
            currentImageIndex = 0;
            System.out.println("Current index reset to: " + currentImageIndex);

            // Cek apakah event lebih dari satu
            boolean hasMultipleEvents = activeEvents.size() > 1;
            System.out.println("Has multiple events: " + hasMultipleEvents);

            prevButton.setVisible(hasMultipleEvents);
            nextButton.setVisible(hasMultipleEvents);

            // Debug button properties
            debugButtonProperties();

            // 3. Tampilkan event pertama
            showEventAtIndex(0);

            // 4. Setup event handlers
            setupEventHandlers();

            System.out.println("=== EventModalController Initialize COMPLETE ===");

        } catch (Exception e) {
            System.err.println("Error in initialize(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void debugButtonProperties() {
        System.out.println("=== Button Debug Info ===");
        System.out.println("Prev Button - Visible: " + prevButton.isVisible() +
                " | Disabled: " + prevButton.isDisabled() +
                " | Managed: " + prevButton.isManaged() +
                " | MouseTransparent: " + prevButton.isMouseTransparent());
        System.out.println("Next Button - Visible: " + nextButton.isVisible() +
                " | Disabled: " + nextButton.isDisabled() +
                " | Managed: " + nextButton.isManaged() +
                " | MouseTransparent: " + nextButton.isMouseTransparent());

        // Cek parent containers
        System.out.println("Prev Button Parent MouseTransparent: " + prevButton.getParent().isMouseTransparent());
        System.out.println("Next Button Parent MouseTransparent: " + nextButton.getParent().isMouseTransparent());
    }

    private void setupEventHandlers() {
        System.out.println("Setting up event handlers...");

        // Setup close handlers
        overlayBackground.setOnMouseClicked(e -> {
            System.out.println("Overlay clicked - closing modal");
            handleClose();
        });

        modalContainer.setOnMouseClicked(e -> {
            System.out.println("Modal container clicked - consuming event");
            e.consume();
        });

        // Setup additional button event handlers for debugging
        prevButton.setOnMousePressed((MouseEvent e) -> {
            System.out.println(">>> PREV BUTTON MOUSE PRESSED <<<");
            e.consume();
        });

        prevButton.setOnMouseReleased((MouseEvent e) -> {
            System.out.println(">>> PREV BUTTON MOUSE RELEASED <<<");
            e.consume();
        });

        prevButton.setOnMouseClicked((MouseEvent e) -> {
            System.out.println(">>> PREV BUTTON MOUSE CLICKED <<<");
            handlePrevImage();
            e.consume();
        });

        nextButton.setOnMousePressed((MouseEvent e) -> {
            System.out.println(">>> NEXT BUTTON MOUSE PRESSED <<<");
            e.consume();
        });

        nextButton.setOnMouseReleased((MouseEvent e) -> {
            System.out.println(">>> NEXT BUTTON MOUSE RELEASED <<<");
            e.consume();
        });

        nextButton.setOnMouseClicked((MouseEvent e) -> {
            System.out.println(">>> NEXT BUTTON MOUSE CLICKED <<<");
            handleNextImage();
            e.consume();
        });

        // Force button properties
        Platform.runLater(() -> {
            prevButton.setMouseTransparent(false);
            nextButton.setMouseTransparent(false);
            prevButton.setDisable(false);
            nextButton.setDisable(false);
            System.out.println("Button properties forced in Platform.runLater()");
        });
    }

    @FXML
    private void handleClose() {
        System.out.println("handleClose() called");
        if (modalStage != null) {
            modalStage.close();
        }
    }

    @FXML
    private void handlePrevImage() {
        System.out.println(">>> handlePrevImage() CALLED <<<");
        System.out.println("Current index before: " + currentImageIndex);
        System.out.println("Total events: " + (activeEvents != null ? activeEvents.size() : "null"));

        // Validasi data
        if (activeEvents == null || activeEvents.isEmpty()) {
            System.err.println("ERROR: No active events available");
            return;
        }

        // Navigasi ke event sebelumnya
        int oldIndex = currentImageIndex;
        currentImageIndex--;
        if (currentImageIndex < 0) {
            currentImageIndex = activeEvents.size() - 1;
        }

        System.out.println("Index changed from " + oldIndex + " to " + currentImageIndex);
        showEventAtIndex(currentImageIndex);
    }

    @FXML
    private void handleNextImage() {
        System.out.println(">>> handleNextImage() CALLED <<<");
        System.out.println("Current index before: " + currentImageIndex);
        System.out.println("Total events: " + (activeEvents != null ? activeEvents.size() : "null"));

        // Validasi data
        if (activeEvents == null || activeEvents.isEmpty()) {
            System.err.println("ERROR: No active events available");
            return;
        }

        // Navigasi ke event berikutnya
        int oldIndex = currentImageIndex;
        currentImageIndex++;
        if (currentImageIndex >= activeEvents.size()) {
            currentImageIndex = 0;
        }

        System.out.println("Index changed from " + oldIndex + " to " + currentImageIndex);
        showEventAtIndex(currentImageIndex);
    }

    private void showEventAtIndex(int index) {
        System.out.println(">>> showEventAtIndex(" + index + ") CALLED <<<");

        // Validasi indeks
        if (activeEvents == null || activeEvents.isEmpty()) {
            System.err.println("ERROR: No active events to display");
            return;
        }

        if (index < 0 || index >= activeEvents.size()) {
            System.err.println("ERROR: Invalid index " + index + ", valid range: 0-" + (activeEvents.size() - 1));
            return;
        }

        try {
            // Dapatkan data event dari list berdasarkan indeks
            Event currentEvent = activeEvents.get(index);
            System.out.println("Displaying event: " + currentEvent.getTitle());

            // Update judul
            eventTitleText.setText(currentEvent.getTitle());
            System.out.println("Title updated to: " + currentEvent.getTitle());

            // Update gambar
            String imagePath = currentEvent.getImagePath();
            System.out.println("Image path: " + imagePath);

            if (imagePath != null && !imagePath.trim().isEmpty()) {
                File imageFile = new File(imagePath);
                System.out.println("Image file exists: " + imageFile.exists());
                System.out.println("Image file is file: " + imageFile.isFile());
                System.out.println("Image file absolute path: " + imageFile.getAbsolutePath());

                if (imageFile.exists() && imageFile.isFile()) {
                    try {
                        Image image = new Image(imageFile.toURI().toString());
                        currentEventImageView.setImage(image);
                        System.out.println("SUCCESS: Image loaded and set");
                    } catch (Exception e) {
                        System.err.println("ERROR loading image: " + e.getMessage());
                        currentEventImageView.setImage(null);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("ERROR: Image file not found or not a file: " + imagePath);
                    currentEventImageView.setImage(null);
                }
            } else {
                System.err.println("ERROR: Image path is null or empty");
                currentEventImageView.setImage(null);
            }

        } catch (Exception e) {
            System.err.println("ERROR in showEventAtIndex(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method untuk debugging manual - bisa dipanggil dari luar
    public void debugInfo() {
        System.out.println("=== MANUAL DEBUG INFO ===");
        System.out.println("Active events: " + (activeEvents != null ? activeEvents.size() : "null"));
        System.out.println("Current index: " + currentImageIndex);
        debugButtonProperties();
    }
}
