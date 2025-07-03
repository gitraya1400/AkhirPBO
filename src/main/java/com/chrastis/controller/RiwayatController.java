package com.chrastis.controller;

import com.chrastis.model.Mahasiswa;
import com.chrastis.model.PengajuanKredit;
import com.chrastis.service.PengajuanService;
import com.chrastis.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.List;

public class RiwayatController {
    @FXML private Text titleText;
    @FXML private VBox riwayatList;
    @FXML private Button backButton;

    private Mahasiswa mahasiswa;
    private final PengajuanService pengajuanService = new PengajuanService();

    public void setMahasiswa(Mahasiswa mahasiswa) {
        this.mahasiswa = mahasiswa;
        loadRiwayatData();
    }

    private void loadRiwayatData() {
        if (mahasiswa == null) return;

        List<PengajuanKredit> pengajuanList = pengajuanService.getPengajuanByMahasiswa(mahasiswa.getId());
        riwayatList.getChildren().clear();

        if (pengajuanList.isEmpty()) {
            riwayatList.getChildren().add(new Text("Tidak ada riwayat pengajuan."));
        } else {
            for (PengajuanKredit p : pengajuanList) {
                riwayatList.getChildren().add(new Label("ID: " + p.getId() + ", Status: " + p.getStatus()));
            }
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneManager.navigateTo("/fxml/dashboard.fxml", 1200, 800, (DashboardController controller) -> {
            controller.setUser(mahasiswa);
            controller.initializeData();
        });
    }
}