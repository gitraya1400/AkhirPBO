package com.chrastis.controller;

import com.chrastis.model.Event;
import com.chrastis.service.EventService;
import com.chrastis.util.UIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

public class EventFormController {

    @FXML private Text formTitle;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField locationField;
    @FXML private DatePicker eventDatePicker;
    @FXML private DatePicker deadlinePicker;
    @FXML private Label imagePathLabel;
    @FXML private Button saveButton;

    private final EventService eventService = new EventService();
    private Event eventToEdit = null;
    private Runnable onSaveCallback;
    private File selectedImageFile;

    public void setOnSave(Runnable callback) {
        this.onSaveCallback = callback;
    }

    public void setEventToEdit(Event event) {
        this.eventToEdit = event;
        formTitle.setText("Edit Event");
        titleField.setText(event.getTitle());
        descriptionArea.setText(event.getDescription());
        locationField.setText(event.getLocation());
        eventDatePicker.setValue(event.getEventDate().toLocalDate());
        deadlinePicker.setValue(event.getDeadline().toLocalDate());
        imagePathLabel.setText(event.getImagePath());
        selectedImageFile = new File(event.getImagePath());
    }

    @FXML
    void handleChooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Event");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(saveButton.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            imagePathLabel.setText(selectedImageFile.getAbsolutePath());
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (titleField.getText().isBlank() || eventDatePicker.getValue() == null || deadlinePicker.getValue() == null || selectedImageFile == null) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Input Tidak Valid", "Semua field wajib diisi.", "");
            return;
        }

        LocalDateTime eventDateTime = eventDatePicker.getValue().atStartOfDay();
        LocalDateTime deadlineDateTime = deadlinePicker.getValue().atTime(23, 59, 59);
        String imagePath = selectedImageFile.getAbsolutePath();

        boolean success;
        if (eventToEdit == null) { // Mode Tambah
            Event newEvent = new Event(
                    UUID.randomUUID().toString(),
                    titleField.getText(),
                    descriptionArea.getText(),
                    imagePath,
                    eventDateTime,
                    deadlineDateTime,
                    locationField.getText(),
                    "Aktif"
            );
            success = eventService.createEvent(newEvent);
        } else { // Mode Edit
            Event updatedEvent = new Event(
                    eventToEdit.getId(),
                    titleField.getText(),
                    descriptionArea.getText(),
                    imagePath,
                    eventDateTime,
                    deadlineDateTime,
                    locationField.getText(),
                    eventToEdit.getStatus() // Status tidak diubah dari form ini
            );
            success = eventService.updateEvent(updatedEvent);
        }

        if (success) {
            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data event berhasil disimpan.", "");
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            closeWindow();
        } else {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan data event ke database.", "");
        }
    }

    @FXML
    void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}