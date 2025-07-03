package com.chrastis.controller;

import com.chrastis.model.Lelang;
import com.chrastis.service.LelangService;
import com.chrastis.util.UIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LelangManagementController {
    @FXML private TableView<Lelang> lelangTable;
    @FXML private Button tambahLelangBtn, editLelangBtn, batalkanLelangBtn, finalisasiBtn;
    // ... @FXML untuk kolom-kolom tabel

    private LelangService lelangService = new LelangService();

    @FXML
    public void initialize() {
        // ... Setup kolom-kolom tabel ...

        tambahLelangBtn.setOnAction(e -> openLelangForm(null));
        editLelangBtn.setOnAction(e -> {
            Lelang selected = lelangTable.getSelectionModel().getSelectedItem();
            if (selected != null) openLelangForm(selected);
        });
        finalisasiBtn.setOnAction(e -> handleFinalisasiLelang());
    }

    public void initializeData() {
        refreshTable();
    }

    private void refreshTable() {
        lelangTable.setItems(FXCollections.observableArrayList(lelangService.getAllLelang()));
    }

    private void openLelangForm(Lelang lelang) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/lelang-form.fxml"));
            Parent root = loader.load();

            LelangFormController controller = loader.getController();
            controller.setOnSave(this::refreshTable); // Kirim callback untuk refresh tabel
            if (lelang != null) {
                controller.setLelangToEdit(lelang);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(lelang == null ? "Tambah Lelang Baru" : "Edit Lelang");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleFinalisasiLelang() {
        Lelang selected = lelangTable.getSelectionModel().getSelectedItem();
        if (selected == null || !"Aktif".equals(selected.getStatus())) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Pilih Lelang", "Pilih lelang yang berstatus 'Aktif' untuk difinalisasi.", "");
            return;
        }

        UIUtils.showConfirmation("Konfirmasi Finalisasi", "Anda yakin ingin menutup lelang '" + selected.getLokasi() + "'?",
                "Aksi ini akan menentukan pemenang, mengembalikan kredit yang kalah, dan tidak dapat dibatalkan.").ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = lelangService.finalizeLelang(selected.getId());
                if (success) {
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Sukses", "Lelang berhasil difinalisasi!", "Pemenang telah ditentukan dan notifikasi telah dikirim.");
                    refreshTable();
                } else {
                    UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memfinalisasi lelang.", "Terjadi kesalahan pada database.");
                }
            }
        });
    }
}