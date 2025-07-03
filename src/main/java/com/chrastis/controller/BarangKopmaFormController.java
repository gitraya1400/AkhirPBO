package com.chrastis.controller;

import com.chrastis.model.BarangKopma;
import com.chrastis.service.KopmaService;
import com.chrastis.util.UIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class BarangKopmaFormController implements Initializable {
    @FXML private Text formTitle;
    @FXML private TextField namaField, kategoriField;
    @FXML private TextArea deskripsiArea;
    @FXML private Spinner<Integer> hargaSpinner, stokSpinner;
    @FXML private Button saveButton;

    private final KopmaService kopmaService = new KopmaService();
    private BarangKopma barangToEdit = null;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hargaSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 50, 5));
        stokSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 10));
    }

    public void setOnSave(Runnable callback) {
        this.onSaveCallback = callback;
    }

    public void setBarangToEdit(BarangKopma barang) {
        this.barangToEdit = barang;
        formTitle.setText("Edit Barang KOPMA");
        namaField.setText(barang.getNama());
        kategoriField.setText(barang.getKategori());
        deskripsiArea.setText(barang.getDeskripsi());
        hargaSpinner.getValueFactory().setValue(barang.getHargaKredit());
        stokSpinner.getValueFactory().setValue(barang.getStok());
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (namaField.getText().isBlank()) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Input Tidak Valid", "Nama barang tidak boleh kosong.", "");
            return;
        }

        boolean success;
        if (barangToEdit == null) { // Mode Tambah
            BarangKopma newBarang = new BarangKopma(UUID.randomUUID().toString(), namaField.getText(), hargaSpinner.getValue(), stokSpinner.getValue(), deskripsiArea.getText(), kategoriField.getText());
            success = kopmaService.createBarang(newBarang);
        } else { // Mode Edit
            barangToEdit.setNama(namaField.getText());
            barangToEdit.setKategori(kategoriField.getText());
            barangToEdit.setHargaKredit(hargaSpinner.getValue());
            barangToEdit.setStok(stokSpinner.getValue());
            barangToEdit.setDeskripsi(deskripsiArea.getText());
            success = kopmaService.updateBarang(barangToEdit);
        }

        if (success) {
            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data barang berhasil disimpan.", "");
            if (onSaveCallback != null) {
                onSaveCallback.run(); // Menjalankan refresh tabel di controller induk
            }
            closeWindow();
        } else {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan data barang ke database.", "");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}