package com.chrastis.controller;

import com.chrastis.model.KategoriKredit;
import com.chrastis.model.Mahasiswa;
import com.chrastis.model.PengajuanKredit;
import com.chrastis.model.SubKategoriKredit;
import com.chrastis.service.KreditService;
import com.chrastis.service.PengajuanService;
import com.chrastis.util.SceneManager;
import com.chrastis.util.UIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import java.util.UUID;

public class InputKreditController implements Initializable {

    // FXML Main components
    @FXML private ComboBox<KategoriKredit> kategoriCombo;
    @FXML private ComboBox<SubKategoriKredit> subKategoriCombo;
    @FXML private TextArea deskripsiArea;
    @FXML private DatePicker tanggalPicker;
    @FXML private Button uploadButton;
    @FXML private Label fileLabel;
    @FXML private Label totalPoinLabel;

    private Mahasiswa mahasiswa;
    private File selectedFile;
    private final PengajuanService pengajuanService = new PengajuanService();
    private final KreditService kreditService = new KreditService(); // Service untuk ambil data kategori

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Mengisi ComboBox Kategori Utama
        kategoriCombo.getItems().setAll(kreditService.getAllKategori());

        // Listener untuk Kategori Utama
        kategoriCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Ambil sub-kategori berdasarkan ID kategori yang dipilih
                subKategoriCombo.getItems().setAll(kreditService.getSubKategoriByKategoriId(newVal.getId()));
                subKategoriCombo.setDisable(false);
                subKategoriCombo.setPromptText("Pilih Jenis Kegiatan/Prestasi");
            } else {
                subKategoriCombo.getItems().clear();
                subKategoriCombo.setDisable(true);
                subKategoriCombo.setPromptText("Pilih Kategori terlebih dahulu");
            }
            updateTotalPoin(); // Reset poin jika kategori berubah
        });

        // Listener untuk Sub-Kategori untuk update poin
        subKategoriCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateTotalPoin();
        });
    }

    private void updateTotalPoin() {
        SubKategoriKredit selected = subKategoriCombo.getSelectionModel().getSelectedItem();
        if (selected != null) {
            totalPoinLabel.setText(String.valueOf(selected.getPoin()));
        } else {
            totalPoinLabel.setText("0");
        }
    }

    public void setMahasiswa(Mahasiswa mahasiswa) {
        this.mahasiswa = mahasiswa;
    }

    public void initializeData() { }

    @FXML
    void handleSubmit(ActionEvent event) {
        KategoriKredit kategori = kategoriCombo.getValue();
        SubKategoriKredit subKategori = subKategoriCombo.getValue();

        if (kategori == null || subKategori == null || tanggalPicker.getValue() == null || selectedFile == null) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", null, "Semua field wajib diisi, termasuk file bukti.");
            return;
        }

        String buktiPath = saveUploadedFile();
        if (buktiPath == null) return; // Gagal menyimpan file

        PengajuanKredit pengajuan = new PengajuanKredit(
                UUID.randomUUID().toString(),
                mahasiswa,
                String.valueOf(subKategori.getId()), // Simpan ID sub-kategori sebagai referensi
                buktiPath,
                tanggalPicker.getValue()
        );
        pengajuan.setNilai(subKategori.getPoin());
        pengajuan.setKategori(kategori.getNamaKategori()); // Simpan nama untuk display
        pengajuan.setSubKategori(subKategori.getNamaSubKategori()); // Simpan nama untuk display
        pengajuan.setDeskripsi(deskripsiArea.getText());

        if (pengajuanService.tambahPengajuan(pengajuan)) {
            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Sukses", "Pengajuan berhasil dikirim!", "Silakan tunggu verifikasi dari admin.");
            handleBack(null);
        } else {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", "Database Error", "Gagal menyimpan pengajuan ke database.");
        }
    }

    private String saveUploadedFile() {
        try {
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            Path targetPath = uploadDir.resolve(fileName);
            Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath.toString();
        } catch (Exception e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Upload Gagal", "Gagal menyimpan file bukti.", e.getMessage());
            return null;
        }
    }

    @FXML
    void handleFileUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih File Bukti");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Gambar & PDF", "*.png", "*.jpg", "*.jpeg", "*.pdf")
        );
        Stage stage = (Stage) uploadButton.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            fileLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        SceneManager.navigateTo("/fxml/dashboard.fxml", 1200, 800, (DashboardController c) -> {
            c.setUser(mahasiswa);
            c.initializeData();
        });
    }
}