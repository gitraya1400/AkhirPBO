package com.chrastis.controller;

import com.chrastis.model.Lelang;
import com.chrastis.model.Mahasiswa;
import com.chrastis.model.PenawaranLelang;
import com.chrastis.service.LelangService;
import com.chrastis.service.NotifikasiService;
import com.chrastis.service.UserService;
import com.chrastis.util.SceneManager;
import com.chrastis.util.UIUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LelangController implements Initializable {

    //<editor-fold desc="FXML Declarations">
    @FXML private Text kreditText, detailLokasiText, detailDeskripsiText, detailMinKreditText, detailTglSelesaiText;
    @FXML private TableView<Lelang> lelangTable;
    @FXML private TableColumn<Lelang, String> lokasiColumn;
    @FXML private TableColumn<Lelang, Integer> kuotaColumn, tahunLelangColumn;
    @FXML private VBox detailPane;
    @FXML private HBox bidBox;
    @FXML private Spinner<Integer> bidSpinner;
    @FXML private Button ikutLelangButton, tarikTawaranButton, backButton;
    @FXML private TableView<PenawaranLelang> peringkatTable;
    @FXML private TableColumn<PenawaranLelang, Integer> peringkatRankColumn, peringkatTawaranColumn;
    @FXML private TableColumn<PenawaranLelang, String> peringkatNamaColumn;
    @FXML private ComboBox<String> tahunLelangFilterCombo;
    //</editor-fold>

    private Mahasiswa mahasiswa;
    private Lelang lelangTerpilih;
    private final LelangService lelangService = new LelangService();
    private final NotifikasiService notifikasiService = new NotifikasiService();
    private final UserService userService = new UserService();

    private final ObservableList<Lelang> lelangMasterList = FXCollections.observableArrayList();
    private FilteredList<Lelang> filteredLelangList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupSelectionListener();
        setupBidSpinner();
        setupLelangFilter();
        detailPane.setVisible(false);
    }

    public void setMahasiswa(Mahasiswa mahasiswa) {
        this.mahasiswa = mahasiswa;
    }

    public void initializeData() {
        if (mahasiswa != null) {
            refreshMahasiswaData();
            lelangMasterList.setAll(lelangService.getLelangAktif());
            populateFilterComboBox();
        }
    }

    private void refreshMahasiswaData() {
        if (mahasiswa == null) return;
        userService.getUserById(mahasiswa.getId()).ifPresent(updatedUser -> {
            if (updatedUser instanceof Mahasiswa) {
                this.mahasiswa = (Mahasiswa) updatedUser;
                kreditText.setText(String.valueOf(this.mahasiswa.getTotalKredit()));
            }
        });
    }

    private void setupBidSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0, 10);
        bidSpinner.setValueFactory(valueFactory);
    }

    private void setupTableColumns() {
        lokasiColumn.setCellValueFactory(new PropertyValueFactory<>("lokasi"));
        kuotaColumn.setCellValueFactory(new PropertyValueFactory<>("kuota"));
        tahunLelangColumn.setCellValueFactory(new PropertyValueFactory<>("tahunLelang"));

        peringkatRankColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(peringkatTable.getItems().indexOf(cell.getValue()) + 1).asObject());
        peringkatNamaColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMahasiswa().getNama()));
        peringkatTawaranColumn.setCellValueFactory(new PropertyValueFactory<>("jumlahKreditDitawar"));
    }

    private void setupSelectionListener() {
        lelangTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            lelangTerpilih = newSelection;
            if (newSelection != null) {
                tampilkanDetailLelang(newSelection);
            } else {
                detailPane.setVisible(false);
            }
        });
    }

    private void setupLelangFilter() {
        filteredLelangList = new FilteredList<>(lelangMasterList, p -> true);
        tahunLelangFilterCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));
        lelangTable.setItems(filteredLelangList);
    }

    private void populateFilterComboBox() {
        List<String> tahunList = lelangMasterList.stream()
                .map(lelang -> String.valueOf(lelang.getTahunLelang()))
                .distinct().sorted().collect(Collectors.toList());

        tahunLelangFilterCombo.getItems().clear();
        tahunLelangFilterCombo.getItems().add("Semua Tahun");
        tahunLelangFilterCombo.getItems().addAll(tahunList);
        tahunLelangFilterCombo.setValue("Semua Tahun");
    }

    private void applyFilter(String selectedTahun) {
        filteredLelangList.setPredicate(lelang -> {
            if (selectedTahun == null || "Semua Tahun".equals(selectedTahun)) {
                return true;
            }
            return String.valueOf(lelang.getTahunLelang()).equals(selectedTahun);
        });
    }

    private void tampilkanDetailLelang(Lelang lelang) {
        detailPane.setVisible(true);
        detailLokasiText.setText(lelang.getLokasi());
        detailDeskripsiText.setText(lelang.getDeskripsi() + "\n\n(Formasi untuk lulusan tahun " + lelang.getTahunLelang() + ")");
        detailMinKreditText.setText(lelang.getMinimalKredit() + " poin");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM uuuu, HH:mm");
        detailTglSelesaiText.setText(lelang.getTanggalSelesai() != null ? lelang.getTanggalSelesai().format(formatter) : "N/A");

        List<PenawaranLelang> pendaftar = lelangService.getPendaftarByLelangId(lelang.getId());
        peringkatTable.setItems(FXCollections.observableArrayList(pendaftar));

        Optional<PenawaranLelang> penawaranDiLelangIni = pendaftar.stream()
                .filter(p -> p.getMahasiswa().getId().equals(mahasiswa.getId()))
                .findFirst();

        boolean tahunSesuai = (mahasiswa.getTahunLulus() == lelang.getTahunLelang());
        boolean kreditCukup = mahasiswa.getTotalKredit() >= lelang.getMinimalKredit();

        ikutLelangButton.setDisable(false);

        if (penawaranDiLelangIni.isPresent()) {
            bidBox.setVisible(false); bidBox.setManaged(false);
            tarikTawaranButton.setVisible(true); tarikTawaranButton.setManaged(true);
        } else {
            bidBox.setVisible(true); bidBox.setManaged(true);
            tarikTawaranButton.setVisible(false); tarikTawaranButton.setManaged(false);

            if (!tahunSesuai) {
                ikutLelangButton.setDisable(true);
                ikutLelangButton.setText("Tidak Sesuai Tahun Lulus (" + mahasiswa.getTahunLulus() + ")");
            } else if (!kreditCukup) {
                ikutLelangButton.setDisable(true);
                ikutLelangButton.setText("Kredit Minimal Tidak Cukup");
            } else {
                ikutLelangButton.setDisable(false);
                ikutLelangButton.setText("Tawar Formasi Ini");
            }
        }
    }

    @FXML
    void handleIkutLelang(ActionEvent event) {
        if (lelangTerpilih == null) return;

        if (mahasiswa.getTahunLulus() != lelangTerpilih.getTahunLelang()) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", "Tahun Lulus Tidak Sesuai", "Anda hanya bisa mendaftar pada formasi untuk tahun kelulusan " + lelangTerpilih.getTahunLelang() + ".");
            return;
        }

        int jumlahBid = bidSpinner.getValue();

        if (jumlahBid < lelangTerpilih.getMinimalKredit()) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Tawaran Terlalu Rendah", "Jumlah tawaran Anda harus setidaknya sama dengan kredit minimal.", "Minimal: " + lelangTerpilih.getMinimalKredit());
            return;
        }
        if (jumlahBid > mahasiswa.getTotalKredit()) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Kredit Tidak Cukup", "Jumlah tawaran Anda melebihi total sisa kredit yang Anda miliki.", "Kredit Anda: " + mahasiswa.getTotalKredit());
            return;
        }

        Optional<PenawaranLelang> penawaranLama = lelangService.getPenawaranAktifMahasiswa(mahasiswa.getId());
        String konfirmasiHeader = "Anda akan menawar " + jumlahBid + " kredit pada formasi " + lelangTerpilih.getLokasi() + ".";
        String konfirmasiContent = penawaranLama.map(penawaran -> "Pendaftaran Anda di formasi '" + penawaran.getLelang().getLokasi() + "' akan ditarik dan digantikan dengan yang ini. Lanjutkan?").orElse("Kredit Anda akan dikurangi sejumlah " + jumlahBid + " poin. Lanjutkan?");

        UIUtils.showConfirmation("Konfirmasi Tawaran", konfirmasiHeader, konfirmasiContent)
                .ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        boolean success = lelangService.tambahPenawaran(lelangTerpilih.getId(), mahasiswa.getId(), jumlahBid);
                        if (success) {
                            notifikasiService.tambahNotifikasi(mahasiswa.getId(), "Anda berhasil menawar pada lelang penempatan di " + lelangTerpilih.getLokasi() + ".");
                            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Tawaran berhasil ditempatkan!", "Kredit Anda telah diperbarui.");
                            refreshMahasiswaData();
                            tampilkanDetailLelang(lelangTerpilih);
                        } else {
                            UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menempatkan tawaran.", "Terjadi kesalahan pada database.");
                        }
                    }
                });
    }

    @FXML
    void handleTarikTawaran(ActionEvent event) {
        if (lelangTerpilih == null) return;
        UIUtils.showConfirmation("Konfirmasi Tarik Tawaran", "Anda yakin ingin menarik tawaran dari formasi ini?", "Kredit Anda akan dikembalikan sepenuhnya.")
                .ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        boolean success = lelangService.tarikPenawaran(mahasiswa.getId());
                        if (success) {
                            notifikasiService.tambahNotifikasi(mahasiswa.getId(), "Anda menarik tawaran dari lelang " + lelangTerpilih.getLokasi() + ". Kredit telah dikembalikan.");
                            UIUtils.showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Tawaran berhasil ditarik.", "Kredit Anda telah dikembalikan.");
                            refreshMahasiswaData();
                            tampilkanDetailLelang(lelangTerpilih);
                        } else {
                            UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menarik tawaran.", "Terjadi kesalahan pada database.");
                        }
                    }
                });
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneManager.navigateTo("/fxml/dashboard.fxml", 1200, 800, (DashboardController controller) -> {
            controller.setUser(mahasiswa);
            controller.initializeData();
        });
    }
}