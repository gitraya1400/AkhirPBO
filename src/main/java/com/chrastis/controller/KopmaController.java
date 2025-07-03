package com.chrastis.controller;

import com.chrastis.model.BarangKopma;
import com.chrastis.model.Mahasiswa;
import com.chrastis.model.TransaksiKopma;
import com.chrastis.service.KopmaService;
import com.chrastis.util.SceneManager;
import com.chrastis.util.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class KopmaController implements Initializable {
    @FXML private Text kreditText;
    @FXML private Button backButton;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private TextField searchField;
    @FXML private TableView<BarangKopma> barangTable;
    @FXML private TableColumn<BarangKopma, String> namaBarangColumn;
    @FXML private TableColumn<BarangKopma, String> kategoriColumn;
    @FXML private TableColumn<BarangKopma, Integer> hargaKreditColumn;
    @FXML private TableColumn<BarangKopma, Integer> stokColumn;
    @FXML private TableColumn<BarangKopma, String> deskripsiColumn;
    @FXML private TableColumn<BarangKopma, Void> aksiColumn;
    @FXML private TableView<TransaksiKopma> transaksiTable;
    @FXML private TableColumn<TransaksiKopma, String> transaksiBarangColumn;
    @FXML private TableColumn<TransaksiKopma, String> transaksiKodeColumn;
    @FXML private TableColumn<TransaksiKopma, String> transaksiTanggalColumn;
    @FXML private TableColumn<TransaksiKopma, String> transaksiStatusColumn;

    private Mahasiswa mahasiswa;
    private final KopmaService kopmaService = new KopmaService();
    private ObservableList<BarangKopma> allBarangData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupFilters();
    }

    public void setMahasiswa(Mahasiswa mahasiswa) {
        this.mahasiswa = mahasiswa;
    }

    public void initializeData() {
        if (mahasiswa != null) {
            kreditText.setText(String.valueOf(mahasiswa.getTotalKredit()));
            loadBarangData();
            loadTransaksiData();
            setupCategoryFilter();
        }
    }

    private void setupFilters() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterBarangData());
    }

    private void setupCategoryFilter() {
        List<String> categories = allBarangData.stream()
                .map(BarangKopma::getKategori)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        categoryFilter.getItems().clear();
        categoryFilter.getItems().add("Semua Kategori");
        categoryFilter.getItems().addAll(categories);
        categoryFilter.setValue("Semua Kategori");
    }

    @FXML
    private void handleCategoryFilter(ActionEvent event) {
        filterBarangData();
    }

    private void filterBarangData() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = categoryFilter.getValue();

        List<BarangKopma> filteredList = allBarangData.stream()
                .filter(barang -> {
                    boolean matchesSearch = searchText.isEmpty() ||
                            barang.getNama().toLowerCase().contains(searchText) ||
                            (barang.getDeskripsi() != null && barang.getDeskripsi().toLowerCase().contains(searchText));
                    boolean matchesCategory = selectedCategory == null || "Semua Kategori".equals(selectedCategory) ||
                            selectedCategory.equals(barang.getKategori());
                    return matchesSearch && matchesCategory;
                })
                .collect(Collectors.toList());
        barangTable.setItems(FXCollections.observableArrayList(filteredList));
    }

    private void setupTableColumns() {
        namaBarangColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        kategoriColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        hargaKreditColumn.setCellValueFactory(new PropertyValueFactory<>("hargaKredit"));
        stokColumn.setCellValueFactory(new PropertyValueFactory<>("stok"));
        deskripsiColumn.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));

        aksiColumn.setCellFactory(param -> new TableCell<>() {
            private final Button tukarButton = new Button("🛒 Tukar");
            {
                tukarButton.getStyleClass().add("action-btn-success");
                tukarButton.setOnAction(e -> handleTukarBarang(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    BarangKopma barang = getTableView().getItems().get(getIndex());
                    tukarButton.setDisable(barang.getStok() <= 0 || mahasiswa.getTotalKredit() < barang.getHargaKredit());
                    setGraphic(tukarButton);
                }
            }
        });

        transaksiBarangColumn.setCellValueFactory(cellData -> {
            String barangNama = kopmaService.getAllBarangKopma().stream()
                    .filter(b -> b.getId().equals(cellData.getValue().getBarangId()))
                    .findFirst().map(BarangKopma::getNama).orElse("Tidak Diketahui");
            return new SimpleStringProperty(barangNama);
        });
        transaksiKodeColumn.setCellValueFactory(new PropertyValueFactory<>("kodePenukaran"));
        transaksiTanggalColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTanggal().toString()));
        transaksiStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void handleTukarBarang(BarangKopma barang) {
        if (mahasiswa.getTotalKredit() < barang.getHargaKredit()) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Kredit Tidak Cukup", null, "Kredit Anda tidak cukup untuk menukar barang ini.");
            return;
        }

        String content = String.format("Anda akan menukar %d kredit dengan barang: %s.", barang.getHargaKredit(), barang.getNama());
        UIUtils.showConfirmation("Konfirmasi Penukaran", content, "Lanjutkan?").ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = kopmaService.tukarBarang(mahasiswa.getId(), barang.getId(), barang.getHargaKredit());
                if (success) {
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Berhasil", null, "Penukaran berhasil! Silakan ambil barang di KOPMA dengan kode penukaran.");
                    initializeData();
                } else {
                    UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", null, "Gagal melakukan penukaran. Stok mungkin sudah habis.");
                }
            }
        });
    }

    private void loadBarangData() {
        List<BarangKopma> barangList = kopmaService.getAllBarangKopma();
        allBarangData.setAll(barangList);
        filterBarangData();
    }

    private void loadTransaksiData() {
        List<TransaksiKopma> transaksiList = kopmaService.getTransaksiByMahasiswa(mahasiswa.getId());
        transaksiTable.setItems(FXCollections.observableArrayList(transaksiList));
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneManager.navigateTo("/fxml/dashboard.fxml", 1200, 800, (DashboardController controller) -> {
            controller.setUser(mahasiswa);
            controller.initializeData();
        });
    }
}