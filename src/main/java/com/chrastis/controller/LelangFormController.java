package com.chrastis.controller;

import com.chrastis.model.Lelang;
import com.chrastis.service.LelangService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.UUID;

public class LelangFormController implements Initializable {
    @FXML private Text formTitle;
    @FXML private TextField lokasiField;
    @FXML private TextArea deskripsiArea;
    @FXML private Spinner<Integer> minKreditSpinner, kuotaSpinner, tahunSpinner;
    @FXML private DatePicker tanggalSelesaiPicker;
    @FXML private Button saveButton;

    private final LelangService lelangService = new LelangService();
    private Lelang lelangToEdit = null;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        minKreditSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5000, 100, 10));
        kuotaSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        tahunSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(LocalDate.now().getYear(), LocalDate.now().getYear() + 5, LocalDate.now().getYear()));
    }

    public void setOnSave(Runnable callback) {
        this.onSaveCallback = callback;
    }

    public void setLelangToEdit(Lelang lelang) {
        this.lelangToEdit = lelang;
        formTitle.setText("Edit Formasi Lelang");
        lokasiField.setText(lelang.getLokasi());
        deskripsiArea.setText(lelang.getDeskripsi());
        minKreditSpinner.getValueFactory().setValue(lelang.getMinimalKredit());
        kuotaSpinner.getValueFactory().setValue(lelang.getKuota());
        tahunSpinner.getValueFactory().setValue(lelang.getTahunLelang());
        if (lelang.getTanggalSelesai() != null) {
            tanggalSelesaiPicker.setValue(lelang.getTanggalSelesai().toLocalDate());
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (lokasiField.getText().isBlank() || tanggalSelesaiPicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validasi Gagal", "Lokasi dan tanggal selesai wajib diisi.");
            return;
        }

        LocalDateTime tanggalSelesai = tanggalSelesaiPicker.getValue().atTime(23, 59, 59);
        boolean success;

        if (lelangToEdit == null) { // Mode Tambah
            Lelang newLelang = new Lelang(
                    UUID.randomUUID().toString(),
                    lokasiField.getText(),
                    minKreditSpinner.getValue(),
                    kuotaSpinner.getValue(),
                    tahunSpinner.getValue(),
                    deskripsiArea.getText(),
                    tanggalSelesai,
                    "Aktif"
            );
            success = lelangService.createLelang(newLelang);
        } else { // Mode Edit
            lelangToEdit.setLokasi(lokasiField.getText());
            lelangToEdit.setDeskripsi(deskripsiArea.getText());
            lelangToEdit.setMinimalKredit(minKreditSpinner.getValue());
            lelangToEdit.setKuota(kuotaSpinner.getValue());
            lelangToEdit.setTahunLelang(tahunSpinner.getValue());
            lelangToEdit.setTanggalSelesai(tanggalSelesai);
            success = lelangService.updateLelang(lelangToEdit);
        }

        if (success) {
            if (onSaveCallback != null) {
                onSaveCallback.run(); // Jalankan callback untuk refresh tabel
            }
            showAlert(Alert.AlertType.INFORMATION, "Sukses", lelangToEdit == null ? "Lelang baru berhasil ditambahkan." : "Lelang berhasil diperbarui.");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan lelang. Periksa koneksi atau data.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}