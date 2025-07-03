package com.chrastis.controller;

import com.chrastis.model.*;
import com.chrastis.service.*;
import com.chrastis.util.SceneManager;
import com.chrastis.util.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.scene.Node; // Import ini mungkin diperlukan
import java.util.stream.Stream;

public class AdminController implements Initializable {

    // FXML Declarations (Lama)
    @FXML private Text headerTitle;
    @FXML private ListView<String> adminMenuListView;
    @FXML private VBox verifikasiKreditPane, riwayatKreditPane, manajemenLelangPane, verifikasiKopmaPane, riwayatKopmaPane, manajemenKopmaPane;
    @FXML private Label pendingCountLabel;
    @FXML private TableView<PengajuanKredit> pengajuanTable;
    @FXML private TableColumn<PengajuanKredit, String> pengajuanNamaColumn, pengajuanKategoriColumn, pengajuanDeskripsiColumn;
    @FXML private TableColumn<PengajuanKredit, Void> pengajuanBuktiColumn, pengajuanAksiColumn;
    @FXML private TableColumn<PengajuanKredit, Integer> pengajuanPoinColumn;
    @FXML private TableView<PengajuanKredit> riwayatAdminTable;
    @FXML private TableColumn<PengajuanKredit, String> riwayatAdminNamaColumn, riwayatAdminKategoriColumn, riwayatAdminStatusColumn, riwayatAdminKeteranganColumn;
    @FXML private TableColumn<PengajuanKredit, Void> riwayatAdminBuktiColumn;
    @FXML private TableColumn<PengajuanKredit, Integer> riwayatAdminPoinColumn;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private TableView<Lelang> lelangTable;
    @FXML private TableColumn<Lelang, String> lelangLokasiColumn, lelangStatusColumn;
    @FXML private TableColumn<Lelang, Integer> lelangTahunColumn, lelangKuotaColumn, lelangMinKreditColumn;
    @FXML private Button tambahLelangBtn, editLelangBtn, batalkanLelangBtn, finalisasiBtn;
    @FXML private TableView<BarangKopma> barangKopmaTable;
    @FXML private TableColumn<BarangKopma, String> barangNamaColumn, barangKategoriColumn;
    @FXML private TableColumn<BarangKopma, Integer> barangHargaColumn, barangStokColumn;
    @FXML private Button tambahBarangBtn, editBarangBtn, hapusBarangBtn;
    @FXML private TableView<TransaksiKopma> transaksiTable;
    @FXML private TableColumn<TransaksiKopma, String> transaksiNamaColumn, transaksiBarangColumn, transaksiKodeColumn;
    @FXML private TableColumn<TransaksiKopma, Void> transaksiAksiColumn;
    @FXML private TableView<TransaksiKopma> riwayatKopmaTable;
    @FXML private TableColumn<TransaksiKopma, String> riwayatKopmaNamaColumn, riwayatKopmaBarangColumn, riwayatKopmaTanggalColumn, riwayatKopmaStatusColumn;
    @FXML private Button backButton;

    // FXML Declarations (BARU untuk Manajemen Event)
    @FXML private VBox manajemenEventPane;
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> eventTitleColumn, eventDateColumn, eventDeadlineColumn, eventStatusColumn;
    @FXML private Button tambahEventBtn, editEventBtn, hapusEventBtn;

    // TAMBAHKAN FXML Declaration untuk VBox menu
    @FXML private VBox kreditMenuBox;
    @FXML private VBox kopmaMenuBox;

    // Services & State (Lama)
    private User currentUser;
    private final PengajuanService pengajuanService = new PengajuanService();
    private final TransaksiService transaksiService = new TransaksiService();
    private final UserService userService = new UserService();
    private final NotifikasiService notifikasiService = new NotifikasiService();
    private final LelangService lelangService = new LelangService();
    private final KopmaService kopmaService = new KopmaService();
    private final ObservableList<PengajuanKredit> riwayatKreditMaster = FXCollections.observableArrayList();

    // Variabel untuk melacak tombol mana yang sedang aktif
    private Button activeMenuButton;

    // Services (BARU)
    private final EventService eventService = new EventService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        adminMenuListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
//            if (newVal != null) switchView(newVal);
//        });
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    public void initializeData() {
        if (currentUser == null) {
            System.out.println("User is null, skipping initialization");
            return;
        }
        setupRoleBasedMenus();
    }

    private void setupRoleBasedMenus() {
        kreditMenuBox.setVisible(false); kreditMenuBox.setManaged(false);
        kopmaMenuBox.setVisible(false); kopmaMenuBox.setManaged(false);

        String firstView = "";
        if (currentUser instanceof AdminKredit) {
            headerTitle.setText("⚙️ Admin Panel - Kredit & Lelang");
            kreditMenuBox.setVisible(true); kreditMenuBox.setManaged(true);
            firstView = "Verifikasi Kredit";
        } else if (currentUser instanceof AdminKopma) {
            headerTitle.setText("⚙️ Admin Panel - KOPMA");
            kopmaMenuBox.setVisible(true); kopmaMenuBox.setManaged(true);
            firstView = "Verifikasi KOPMA";
        } else {
            System.out.println("Unknown admin type: " + currentUser.getClass().getSimpleName());
        }

        // Otomatis klik tombol pertama
        if (!firstView.isEmpty()) {
            // Temukan tombol pertama dan set sebagai aktif
            findButtonByText(firstView).ifPresent(this::setActiveButton);
            switchView(firstView);
        }
    }

    // TAMBAHKAN METODE BARU INI untuk menangani semua klik tombol menu
    @FXML
    private void handleAdminMenuClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String viewName = clickedButton.getText();

        // Ganti style tombol aktif
        setActiveButton(clickedButton);

        // Panggil metode yang sudah ada untuk ganti view
        switchView(viewName);
    }

    // TAMBAHKAN METODE BARU INI untuk mengubah style tombol aktif
    private void setActiveButton(Button button) {
        // Hapus style 'active' dari tombol sebelumnya (jika ada)
        if (activeMenuButton != null) {
            activeMenuButton.getStyleClass().remove("active");
        }
        // Tambahkan style 'active' ke tombol yang baru diklik
        button.getStyleClass().add("active");
        // Simpan referensi tombol yang aktif saat ini
        activeMenuButton = button;
    }

    // TAMBAHKAN METODE BARU INI untuk mencari tombol berdasarkan teksnya
    private java.util.Optional<Button> findButtonByText(String text) {
        Stream<Node> allButtons = Stream.concat(
                kreditMenuBox.getChildren().stream(),
                kopmaMenuBox.getChildren().stream()
        );

        return allButtons
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .filter(button -> button.getText().equals(text))
                .findFirst();
    }

    private void switchView(String viewName) {
        verifikasiKreditPane.setVisible(false); verifikasiKreditPane.setManaged(false);
        riwayatKreditPane.setVisible(false); riwayatKreditPane.setManaged(false);
        manajemenLelangPane.setVisible(false); manajemenLelangPane.setManaged(false);
        verifikasiKopmaPane.setVisible(false); verifikasiKopmaPane.setManaged(false);
        riwayatKopmaPane.setVisible(false); riwayatKopmaPane.setManaged(false);
        manajemenKopmaPane.setVisible(false); manajemenKopmaPane.setManaged(false);
        // Sembunyikan pane event
        manajemenEventPane.setVisible(false); manajemenEventPane.setManaged(false);

        switch (viewName) {
            case "Verifikasi Kredit":
                verifikasiKreditPane.setVisible(true); verifikasiKreditPane.setManaged(true);
                setupAndLoadVerifikasiKredit();
                break;
            case "Riwayat Kredit":
                riwayatKreditPane.setVisible(true); riwayatKreditPane.setManaged(true);
                setupAndLoadRiwayatKredit();
                break;
            case "Manajemen Lelang":
                manajemenLelangPane.setVisible(true); manajemenLelangPane.setManaged(true);
                setupAndLoadManajemenLelang();
                break;
            // Tambahkan case untuk event
            case "Manajemen Event":
                manajemenEventPane.setVisible(true); manajemenEventPane.setManaged(true);
                setupAndLoadManajemenEvent();
                break;
            case "Verifikasi KOPMA":
                verifikasiKopmaPane.setVisible(true); verifikasiKopmaPane.setManaged(true);
                setupAndLoadVerifikasiKopma();
                break;
            case "Riwayat KOPMA":
                riwayatKopmaPane.setVisible(true); riwayatKopmaPane.setManaged(true);
                setupAndLoadRiwayatKopma();
                break;
            case "Manajemen Barang KOPMA":
                manajemenKopmaPane.setVisible(true); manajemenKopmaPane.setManaged(true);
                setupAndLoadManajemenKopma();
                break;
            default:
                System.out.println("Unknown view: " + viewName);
        }
    }

    // ==========================================================
    // METODE-METODE BARU UNTUK MANAJEMEN EVENT
    // ==========================================================

    private void setupAndLoadManajemenEvent() {
        if (eventTitleColumn.getCellValueFactory() == null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
            eventTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            eventDateColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEventDate().format(formatter)));
            eventDeadlineColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDeadline().format(formatter)));
            eventStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        }
        refreshEventTable();
    }

    private void refreshEventTable() {
        eventTable.setItems(FXCollections.observableArrayList(eventService.getAllEvents()));
    }

    @FXML
    private void handleTambahEvent() {
        openEventForm(null);
    }

    @FXML
    private void handleEditEvent() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            openEventForm(selectedEvent);
        } else {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Pilih Event", "Pilih event yang ingin Anda edit.", "");
        }
    }

    @FXML
    private void handleHapusEvent() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            UIUtils.showConfirmation("Konfirmasi Hapus", "Anda yakin ingin menghapus event '" + selectedEvent.getTitle() + "'?", "Aksi ini tidak dapat dibatalkan.").ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (eventService.deleteEvent(selectedEvent.getId())) {
                        UIUtils.showAlert(Alert.AlertType.INFORMATION, "Sukses", "Event berhasil dihapus.", "");
                        refreshEventTable();
                    } else {
                        UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus event.", "");
                    }
                }
            });
        } else {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Pilih Event", "Pilih event yang ingin Anda hapus.", "");
        }
    }

    private void openEventForm(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/event-form.fxml"));
            Parent root = loader.load();
            EventFormController controller = loader.getController();
            controller.setOnSave(this::refreshEventTable);
            if (event != null) {
                controller.setEventToEdit(event);
            }
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(event == null ? "Tambah Event Baru" : "Edit Event");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka form event.", e.getMessage());
        }
    }

    // ==========================================================
    // METODE-METODE LAMA (TIDAK PERLU DIUBAH)
    // ==========================================================

    // (Semua metode lain seperti setupAndLoadVerifikasiKredit, handleApprove, dll. tetap ada di sini)
    // ...
    // Salin semua metode lama Anda yang belum ada di sini
    // ...

    private void setupAndLoadVerifikasiKredit() {
        if (pengajuanNamaColumn.getCellValueFactory() == null) {
            pengajuanNamaColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMahasiswa().getNama()));
            pengajuanPoinColumn.setCellValueFactory(new PropertyValueFactory<>("nilai"));
            pengajuanKategoriColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
            pengajuanDeskripsiColumn.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
            pengajuanBuktiColumn.setCellFactory(p -> createViewFileCell(PengajuanKredit::getBukti));
            pengajuanAksiColumn.setCellFactory(p -> createKreditActionCell());
        }
        ObservableList<PengajuanKredit> pendingList = FXCollections.observableArrayList(pengajuanService.getPendingPengajuan());
        pengajuanTable.setItems(pendingList);
        pendingCountLabel.setText("Menunggu: " + pendingList.size());
    }

    private void setupAndLoadRiwayatKredit() {
        if (riwayatAdminNamaColumn.getCellValueFactory() == null) {
            riwayatAdminNamaColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMahasiswa().getNama()));
            riwayatAdminPoinColumn.setCellValueFactory(new PropertyValueFactory<>("nilai"));
            riwayatAdminKategoriColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
            riwayatAdminStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            riwayatAdminKeteranganColumn.setCellValueFactory(new PropertyValueFactory<>("keterangan"));
            riwayatAdminBuktiColumn.setCellFactory(p -> createViewFileCell(PengajuanKredit::getBukti));

            if (statusFilterCombo.getItems().isEmpty()) {
                statusFilterCombo.getItems().addAll("Semua", "Menunggu", "Disetujui", "Ditolak");
            }
            statusFilterCombo.setValue("Semua");

            FilteredList<PengajuanKredit> filteredData = new FilteredList<>(riwayatKreditMaster, p -> true);
            statusFilterCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                    filteredData.setPredicate(p -> newVal == null || "Semua".equals(newVal) || newVal.equals(p.getStatus()))
            );
            riwayatAdminTable.setItems(filteredData);
        }
        riwayatKreditMaster.setAll(pengajuanService.getAllPengajuan());
    }

    private void setupAndLoadManajemenLelang() {
        if (lelangLokasiColumn.getCellValueFactory() == null) {
            lelangLokasiColumn.setCellValueFactory(new PropertyValueFactory<>("lokasi"));
            lelangTahunColumn.setCellValueFactory(new PropertyValueFactory<>("tahunLelang"));
            lelangKuotaColumn.setCellValueFactory(new PropertyValueFactory<>("kuota"));
            lelangMinKreditColumn.setCellValueFactory(new PropertyValueFactory<>("minimalKredit"));
            lelangStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        }
        lelangTable.setItems(FXCollections.observableArrayList(lelangService.getAllLelang()));
    }

    private void setupAndLoadVerifikasiKopma() {
        if (transaksiNamaColumn.getCellValueFactory() == null) {
            transaksiNamaColumn.setCellValueFactory(cell -> new SimpleStringProperty(userService.getUserById(cell.getValue().getMahasiswaId()).map(User::getNama).orElse("N/A")));
            transaksiBarangColumn.setCellValueFactory(cell -> new SimpleStringProperty(kopmaService.getBarangById(cell.getValue().getBarangId()).map(BarangKopma::getNama).orElse("N/A")));
            transaksiKodeColumn.setCellValueFactory(new PropertyValueFactory<>("kodePenukaran"));
            transaksiAksiColumn.setCellFactory(p -> createKopmaActionCell());
        }
        transaksiTable.setItems(FXCollections.observableArrayList(transaksiService.getAllWaitingTransaksi()));
    }

    private void setupAndLoadRiwayatKopma() {
        if (riwayatKopmaNamaColumn.getCellValueFactory() == null) {
            riwayatKopmaNamaColumn.setCellValueFactory(cell -> new SimpleStringProperty(userService.getUserById(cell.getValue().getMahasiswaId()).map(User::getNama).orElse("N/A")));
            riwayatKopmaBarangColumn.setCellValueFactory(cell -> new SimpleStringProperty(kopmaService.getBarangById(cell.getValue().getBarangId()).map(BarangKopma::getNama).orElse("N/A")));
            riwayatKopmaTanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
            riwayatKopmaStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        }
        riwayatKopmaTable.setItems(FXCollections.observableArrayList(transaksiService.getAllTransaksi()));
    }

    private void setupAndLoadManajemenKopma() {
        if (barangNamaColumn.getCellValueFactory() == null) {
            barangNamaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
            barangKategoriColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
            barangHargaColumn.setCellValueFactory(new PropertyValueFactory<>("hargaKredit"));
            barangStokColumn.setCellValueFactory(new PropertyValueFactory<>("stok"));
        }
        barangKopmaTable.setItems(FXCollections.observableArrayList(kopmaService.getAllBarangKopma()));
    }

// Replace the handleBack method in your AdminController.java

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            System.out.println("Attempting to navigate back to dashboard.fxml with user: " + (currentUser != null ? currentUser.getNama() : "null"));

            // Navigate back to dashboard.fxml (NOT admin_dashboard.fxml)
            SceneManager.navigateTo("/fxml/dashboard.fxml", 1200, 800, (DashboardController controller) -> {
                controller.setUser(this.currentUser);
                controller.initializeData();
                System.out.println("Dashboard callback executed for user: " + (currentUser != null ? currentUser.getNama() : "null"));
            });

        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Error", "Gagal kembali ke dashboard.", e.getMessage());
        }
    }
    private void handleApprove(PengajuanKredit pengajuan) {
        UIUtils.showConfirmation("Konfirmasi Persetujuan", "Anda yakin ingin menyetujui pengajuan " + pengajuan.getNilai() + " poin?", "Aksi ini akan menambah poin mahasiswa dan tidak dapat dibatalkan.").ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (pengajuanService.approvePengajuan(pengajuan)) {
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Sukses", null, "Pengajuan disetujui.");
                    setupAndLoadVerifikasiKredit();
                    notifikasiService.tambahNotifikasi(pengajuan.getMahasiswa().getId(), "Pengajuan kredit Anda sebesar " + pengajuan.getNilai() + " poin telah disetujui.");
                } else {
                    UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", null, "Gagal memperbarui database.");
                }
            }
        });
    }

    private void handleReject(PengajuanKredit pengajuan) {
        UIUtils.showTextInputDialog("Tolak Pengajuan", "Masukkan alasan penolakan untuk pengajuan oleh " + pengajuan.getMahasiswa().getNama() + ":", "Alasan:", "").ifPresent(alasan -> {
            if (alasan.isBlank()) {
                UIUtils.showAlert(Alert.AlertType.WARNING, "Peringatan", null, "Alasan penolakan tidak boleh kosong.");
                return;
            }
            if (pengajuanService.rejectPengajuan(pengajuan.getId(), pengajuan.getMahasiswa().getId(), alasan)) {
                UIUtils.showAlert(Alert.AlertType.INFORMATION, "Sukses", null, "Pengajuan telah ditolak.");
                setupAndLoadVerifikasiKredit();
                notifikasiService.tambahNotifikasi(pengajuan.getMahasiswa().getId(), "Pengajuan kredit Anda sebesar " + pengajuan.getNilai() + " poin telah ditolak. Alasan: " + alasan);
            } else {
                UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", null, "Gagal memperbarui database.");
            }
        });
    }

    private void handleVerifyTransaksi(TransaksiKopma transaksi) {
        UIUtils.showConfirmation("Konfirmasi Verifikasi", "Verifikasi transaksi ini sebagai 'Selesai'?", "Pastikan barang sudah diterima oleh mahasiswa.").ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (transaksiService.updateTransaksiStatus(transaksi.getId(), "Selesai")) {
                    notifikasiService.tambahNotifikasi(transaksi.getMahasiswaId(), "Transaksi KOPMA Anda dengan kode " + transaksi.getKodePenukaran() + " telah diverifikasi.");
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Sukses", null, "Transaksi telah diverifikasi.");
                    setupAndLoadVerifikasiKopma();
                } else {
                    UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", null, "Gagal memperbarui status transaksi.");
                }
            }
        });
    }

    private void handleCancelTransaksi(TransaksiKopma transaksi) {
        UIUtils.showConfirmation("Konfirmasi Pembatalan", "Anda yakin ingin membatalkan transaksi ini?", "Stok barang dan kredit mahasiswa akan dikembalikan.").ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (transaksiService.cancelTransaksi(transaksi.getId())) {
                    notifikasiService.tambahNotifikasi(transaksi.getMahasiswaId(), "Transaksi KOPMA Anda dengan kode " + transaksi.getKodePenukaran() + " telah dibatalkan oleh Admin. Kredit Anda telah dikembalikan.");
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Sukses", null, "Transaksi dibatalkan. Stok dan kredit telah dikembalikan.");
                    setupAndLoadVerifikasiKopma();
                } else {
                    UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", null, "Gagal membatalkan transaksi.");
                }
            }
        });
    }

    @FXML
    private void handleTambahLelang(ActionEvent event) {
        openLelangForm(null);
    }

    @FXML
    private void handleEditLelang(ActionEvent event) {
        Lelang selected = lelangTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openLelangForm(selected);
        } else {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih lelang yang ingin diedit.", "");
        }
    }

    @FXML
    private void handleBatalkanLelang(ActionEvent event) {
        Lelang selected = lelangTable.getSelectionModel().getSelectedItem();
        if (selected != null && "Aktif".equals(selected.getStatus())) {
            UIUtils.showConfirmation("Konfirmasi Pembatalan", "Anda yakin ingin membatalkan lelang '" + selected.getLokasi() + "'?", "Aksi ini akan mengembalikan kuota dan tidak dapat dibatalkan.").ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (lelangService.cancelLelang(selected.getId())) {
                        UIUtils.showAlert(Alert.AlertType.INFORMATION, "Sukses", "Lelang dibatalkan.", "");
                        lelangTable.setItems(FXCollections.observableArrayList(lelangService.getAllLelang()));
                    } else {
                        UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal membatalkan lelang.", "");
                    }
                }
            });
        } else {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih lelang yang berstatus 'Aktif'.", "");
        }
    }

    @FXML
    private void handleFinalisasiLelang(ActionEvent event) {
        Lelang selected = lelangTable.getSelectionModel().getSelectedItem();
        if (selected == null || !"Aktif".equals(selected.getStatus())) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Pilihan Tidak Valid", "Pilih lelang yang berstatus 'Aktif' untuk difinalisasi.", "");
            return;
        }
        UIUtils.showConfirmation("Konfirmasi Finalisasi", "Anda yakin ingin menutup lelang '" + selected.getLokasi() + "'?", "Aksi ini akan menentukan pemenang dan tidak dapat dibatalkan.").ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (lelangService.finalizeLelang(selected.getId())) {
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Sukses", "Lelang berhasil difinalisasi!", "Pemenang telah ditentukan dan notifikasi telah dikirim.");
                    lelangTable.setItems(FXCollections.observableArrayList(lelangService.getAllLelang()));
                } else {
                    UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memfinalisasi lelang.", "Terjadi kesalahan pada database.");
                }
            }
        });
    }

    @FXML
    private void handleTambahBarang(ActionEvent event) {
        openBarangForm(null);
    }

    @FXML
    private void handleEditBarang(ActionEvent event) {
        BarangKopma selected = barangKopmaTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openBarangForm(selected);
        } else {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih barang yang ingin diedit.", "");
        }
    }

    @FXML
    private void handleHapusBarang(ActionEvent event) {
        BarangKopma selected = barangKopmaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih barang yang ingin dihapus.", "");
            return;
        }
        UIUtils.showConfirmation("Konfirmasi Hapus", "Anda yakin ingin menghapus barang '" + selected.getNama() + "'?", "Aksi ini tidak dapat dibatalkan.").ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (kopmaService.deleteBarang(selected.getId())) {
                    UIUtils.showAlert(Alert.AlertType.INFORMATION, "Sukses", "Barang berhasil dihapus.", "");
                    barangKopmaTable.setItems(FXCollections.observableArrayList(kopmaService.getAllBarangKopma()));
                } else {
                    UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus barang.", "Barang mungkin masih terikat dengan riwayat transaksi.");
                }
            }
        });
    }

    private void openLelangForm(Lelang lelang) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/lelang-form.fxml"));
            Parent root = loader.load();
            LelangFormController controller = loader.getController();
            controller.setOnSave(() -> lelangTable.setItems(FXCollections.observableArrayList(lelangService.getAllLelang())));
            if (lelang != null) controller.setLelangToEdit(lelang);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(lelang == null ? "Tambah Lelang Baru" : "Edit Lelang");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka form lelang.", e.getMessage());
        }
    }

    private void openBarangForm(BarangKopma barang) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/barang-kopma-form.fxml"));
            Parent root = loader.load();
            BarangKopmaFormController controller = loader.getController();
            controller.setOnSave(() -> barangKopmaTable.setItems(FXCollections.observableArrayList(kopmaService.getAllBarangKopma())));
            if (barang != null) controller.setBarangToEdit(barang);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(barang == null ? "Tambah Barang Baru" : "Edit Barang");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka form barang.", e.getMessage());
        }
    }

    private <T> TableCell<T, Void> createViewFileCell(java.util.function.Function<T, String> extractor) {
        return new TableCell<>() {
            private final Button viewBtn = new Button("Lihat");
            {
                viewBtn.getStyleClass().add("action-btn-info");
                viewBtn.setOnAction(e -> {
                    T item = getTableView().getItems().get(getIndex());
                    if (item != null) openFile(extractor.apply(item));
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        };
    }

    private TableCell<PengajuanKredit, Void> createKreditActionCell() {
        return new TableCell<>() {
            private final Button approveBtn = new Button("Setujui");
            private final Button rejectBtn = new Button("Tolak");
            private final HBox pane = new HBox(5, approveBtn, rejectBtn);
            {
                approveBtn.getStyleClass().add("action-btn-success");
                rejectBtn.getStyleClass().add("action-btn-danger");
                approveBtn.setOnAction(e -> handleApprove(getTableView().getItems().get(getIndex())));
                rejectBtn.setOnAction(e -> handleReject(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    private TableCell<TransaksiKopma, Void> createKopmaActionCell() {
        return new TableCell<>() {
            private final Button verifyBtn = new Button("Verifikasi Selesai");
            private final Button cancelBtn = new Button("Batalkan");
            private final HBox pane = new HBox(5, verifyBtn, cancelBtn);
            {
                verifyBtn.getStyleClass().add("action-btn-success");
                cancelBtn.getStyleClass().add("action-btn-danger");
                verifyBtn.setOnAction(e -> handleVerifyTransaksi(getTableView().getItems().get(getIndex())));
                cancelBtn.setOnAction(e -> handleCancelTransaksi(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    private void openFile(String filePath) {
        if (filePath == null || filePath.isBlank() || !new File(filePath).exists()) {
            UIUtils.showAlert(Alert.AlertType.WARNING, "File Tidak Ditemukan", null, "File bukti tidak ada atau telah dipindahkan.");
            return;
        }
        try {
            Desktop.getDesktop().open(new File(filePath));
        } catch (IOException e) {
            UIUtils.showAlert(Alert.AlertType.ERROR, "Gagal Membuka File", null, e.getMessage());
        }
    }
}