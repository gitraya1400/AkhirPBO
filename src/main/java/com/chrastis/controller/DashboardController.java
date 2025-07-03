package com.chrastis.controller;

import com.chrastis.model.*;
import com.chrastis.service.KreditService;
import com.chrastis.service.NotifikasiService;
import com.chrastis.service.PengajuanService;
import com.chrastis.service.UserService;
import com.chrastis.util.SceneManager;
import com.chrastis.util.UIUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import java.io.IOException; // Mungkin sudah ada, tapi pastikan

// ... (import lain yang sudah ada) ...
import com.chrastis.service.EventService; // <-- Import service baru
import com.chrastis.model.Event; // <-- Import model baru
import javafx.scene.image.Image; // <-- Import untuk gambar
import javafx.scene.image.ImageView; // <-- Import untuk gambar
import java.io.File; // <-- Import untuk file


import com.chrastis.model.LelangPendaftarInfo; // <-- Tambah import
import javafx.collections.transformation.FilteredList;

import com.chrastis.service.LelangService;
import javafx.collections.ObservableList;

public class DashboardController implements Initializable {

    // FXML elements
    @FXML private Text welcomeText, userRole, tahunLulusText;
    @FXML private Label kreditInfo, menungguInfo, disetujuiInfo;
    @FXML private Text welcomeNavText, miniKreditValue, miniMenungguValue;
    @FXML private Button ajukanKreditButton, lelangButton, kopmaButton, adminButton, notificationButton, logoutButton;
    @FXML private Button quickAjukanKredit, quickLelang, quickKopma;
    @FXML private Label totalKreditInfo, disetujuiLabel, menungguLabel;
    @FXML private Button showEventModalButton;
    @FXML private TabPane mainTabPane;
    @FXML private HBox infoTab, tahunLulusBox;
    @FXML private Tab riwayatTab, topTenTab, eventTab;
    @FXML private TableView<Mahasiswa> topTenTable;
    @FXML private TableColumn<Mahasiswa, Integer> rankColumn, kreditColumn;
    @FXML private TableColumn<Mahasiswa, String> namaColumn, nimColumn;
    @FXML private TableView<PengajuanKredit> riwayatTable;
    @FXML private TableColumn<PengajuanKredit, String> riwayatJenisColumn, riwayatSubJenisColumn, riwayatTanggalColumn, riwayatStatusColumn, riwayatKeteranganColumn;
    @FXML private TableColumn<PengajuanKredit, Void> riwayatBuktiColumn;
    @FXML private TableColumn<PengajuanKredit, Integer> riwayatPoinColumn;

    // ADMIN SPECIFIC ELEMENTS - Containers to hide
    // ADMIN SPECIFIC ELEMENTS - Containers to hide
    @FXML private HBox miniStatsContainer; // <-- DIUBAH DARI VBox MENJADI HBox
    @FXML private VBox quickActionsSection;
    @FXML private VBox recentActivitiesSection;
    @FXML private VBox welcomeSection;
    @FXML private HBox mainKpiContainer;
    @FXML private Pane mainContentArea;
    @FXML private ScrollPane mainContentScroll;
    @FXML private VBox sidebarMenuItems;
    @FXML private Label sidebarHeaderText;
    @FXML private HBox sidebarHeader;


    @FXML private VBox lelangPendaftarContainer;
    @FXML private TableView<LelangPendaftarInfo> lelangPendaftarTable;
    @FXML private TableColumn<LelangPendaftarInfo, String> namaPendaftarColumn;
    @FXML private TableColumn<LelangPendaftarInfo, String> nimPendaftarColumn;
    @FXML private TableColumn<LelangPendaftarInfo, String> lokasiLelangColumn;
    @FXML private TableColumn<LelangPendaftarInfo, Integer> tahunLulusPendaftarColumn;
    @FXML private TableColumn<LelangPendaftarInfo, Integer> tawaranPendaftarColumn;
    @FXML private TableColumn<LelangPendaftarInfo, Integer> kuotaLelangColumn;
    @FXML private ComboBox<String> lokasiFilterCombo;
    @FXML private ComboBox<String> tahunLulusFilterCombo;

    // TAMBAHKAN INI
    @FXML private VBox eventListContainer;


    private User user;
    private final EventService eventService = new EventService();
    private final PengajuanService pengajuanService = new PengajuanService();
    private final UserService userService = new UserService();
    private final KreditService kreditService = new KreditService();
    private final NotifikasiService notifikasiService = new NotifikasiService();

    // Service yang dibutuhkan
    private final LelangService lelangService = new LelangService();

    // Variabel untuk menampung data dan filter
    private ObservableList<LelangPendaftarInfo> pendaftarMasterData = FXCollections.observableArrayList();
    private FilteredList<LelangPendaftarInfo> filteredPendaftarData;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Dashboard Controller initialized");
    }

    private void safeSetText(Label label, String text) {
        if (label != null) {
            label.setText(text);
            System.out.println("Set text for Label (id=" + label.getId() + "): " + text);
        } else {
            System.out.println("Label is null when trying to set text: " + text);
        }
    }

    private void safeSetText(Text textNode, String text) {
        if (textNode != null) {
            textNode.setText(text);
            System.out.println("Set text for Text (id=" + textNode.getId() + "): " + text);
        } else {
            System.out.println("Text node is null when trying to set text: " + text);
        }
    }

    private void safeSetVisible(javafx.scene.Node node, boolean visible) {
        if (node != null) {
            node.setVisible(visible);
            node.setManaged(visible);
        } else {
            // Pesan ini akan berhenti muncul setelah fx:id ditambahkan
            // System.out.println("Node is null when trying to set visibility to " + visible);
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Ubah metode initializeData Anda menjadi seperti ini
    public void initializeData() {
        if (user == null) {
            return;
        }
        boolean isAdmin = (user instanceof AdminKopma) || (user instanceof AdminKredit);

        if (isAdmin) {
            setupAdminView();
            // INI BAGIAN YANG DIUBAH: Pengecekan khusus untuk AdminKredit
            if (user instanceof AdminKredit) {
                setupLelangPendaftarView();
            }
        } else if (user instanceof Mahasiswa) {
            setupMahasiswaView();
        }
    }

    // Tambahkan 2 metode ini di dalam controller
    private void setupLelangPendaftarView() {
        safeSetVisible(lelangPendaftarContainer, true);

        namaPendaftarColumn.setCellValueFactory(new PropertyValueFactory<>("namaMahasiswa"));
        nimPendaftarColumn.setCellValueFactory(new PropertyValueFactory<>("nim"));
        lokasiLelangColumn.setCellValueFactory(new PropertyValueFactory<>("lokasiLelang"));
        tahunLulusPendaftarColumn.setCellValueFactory(new PropertyValueFactory<>("tahunLulus"));
        tawaranPendaftarColumn.setCellValueFactory(new PropertyValueFactory<>("jumlahTawaran"));
        kuotaLelangColumn.setCellValueFactory(new PropertyValueFactory<>("kuota"));

        pendaftarMasterData.setAll(lelangService.getAllActivePendaftar());
        filteredPendaftarData = new FilteredList<>(pendaftarMasterData, p -> true);
        lelangPendaftarTable.setItems(filteredPendaftarData);

        lokasiFilterCombo.getItems().add("Semua Lokasi");
        lokasiFilterCombo.getItems().addAll(
                pendaftarMasterData.stream().map(LelangPendaftarInfo::getLokasiLelang).distinct().sorted().collect(Collectors.toList())
        );
        lokasiFilterCombo.setValue("Semua Lokasi");

        tahunLulusFilterCombo.getItems().add("Semua Tahun");
        tahunLulusFilterCombo.getItems().addAll(
                pendaftarMasterData.stream().map(p -> String.valueOf(p.getTahunLulus())).distinct().sorted().collect(Collectors.toList())
        );
        tahunLulusFilterCombo.setValue("Semua Tahun");

        lokasiFilterCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> applyFilters());
        tahunLulusFilterCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> applyFilters());
    }

    private void applyFilters() {
        String lokasi = lokasiFilterCombo.getValue();
        String tahun = tahunLulusFilterCombo.getValue();

        filteredPendaftarData.setPredicate(pendaftar -> {
            boolean lokasiMatch = lokasi == null || "Semua Lokasi".equals(lokasi) || pendaftar.getLokasiLelang().equals(lokasi);
            boolean tahunMatch = tahun == null || "Semua Tahun".equals(tahun) || String.valueOf(pendaftar.getTahunLulus()).equals(tahun);
            return lokasiMatch && tahunMatch;
        });
    }

    private void setupCleanAdminDashboard() {
        System.out.println("Setting up ULTRA MINIMAL ADMIN DASHBOARD for user: " + user.getNama());

        // Hide ALL student elements completely
        hideAllStudentElements();

        // Hide ALL main content area - make it completely empty
        hideAllMainContent();

        // Setup only admin navigation in sidebar
        setupMinimalAdminSidebar();

        // Update admin text fields
        updateAdminTexts();

        System.out.println("Ultra minimal admin dashboard setup completed");
    }

    private void setupAdminView() {
        // Sembunyikan semua elemen yang spesifik untuk mahasiswa
        safeSetVisible(quickActionsSection, false); // <--- LOGIKA KUNCI
        safeSetVisible(recentActivitiesSection, false); // <--- LOGIKA KUNCI
        safeSetVisible(mainKpiContainer, false);
        safeSetVisible(miniStatsContainer, false);
        safeSetVisible(mainTabPane, false);
        safeSetVisible(ajukanKreditButton, false);
        safeSetVisible(lelangButton, false);
        safeSetVisible(kopmaButton, false);
        safeSetVisible(notificationButton, false);
        safeSetVisible(tahunLulusBox, false);

        // Tampilkan elemen yang spesifik untuk admin
        safeSetVisible(adminButton, true);

        safeSetVisible(lelangPendaftarContainer, false);

        // Atur teks
        welcomeText.setText("Admin: " + user.getNama());
        welcomeNavText.setText("Dashboard Administrator");
        String role = (user instanceof AdminKopma) ? "Admin KOPMA" : "Admin Kredit";
        userRole.setText(role);
    }
    private void setupMahasiswaView() {
        // Tampilkan semua elemen yang spesifik untuk mahasiswa
        safeSetVisible(quickActionsSection, true);
        safeSetVisible(recentActivitiesSection, true);
        safeSetVisible(mainKpiContainer, true);
        safeSetVisible(miniStatsContainer, true);
        safeSetVisible(mainTabPane, true);
        safeSetVisible(ajukanKreditButton, true);
        safeSetVisible(lelangButton, true);
        safeSetVisible(kopmaButton, true);
        safeSetVisible(notificationButton, true);
        safeSetVisible(tahunLulusBox, true);

        // Sembunyikan tombol admin
        safeSetVisible(adminButton, false);

        // Atur teks & data mahasiswa
        welcomeText.setText("Selamat Datang, " + user.getNama());
        welcomeNavText.setText("Selamat Datang, " + user.getNama());
        userRole.setText("Mahasiswa");

        if (user instanceof Mahasiswa mahasiswa) {
            loadMahasiswaData(mahasiswa);
            // ==========================================================
            // TAMBAHKAN BARIS INI UNTUK MEMPERBAIKI TAHUN LULUS
            safeSetText(tahunLulusText, String.valueOf(mahasiswa.getTahunLulus()));
            // ==========================================================
    }
    loadLeaderboard();
    loadActiveEventsToDashboard();
    setupTableColumns();
}

    private void hideAllStudentElements() {
        System.out.println("Hiding ALL student elements...");

        // Hide ALL sidebar student buttons
        safeSetVisible(ajukanKreditButton, false);
        safeSetVisible(lelangButton, false);
        safeSetVisible(kopmaButton, false);
        safeSetVisible(notificationButton, false);

        // Hide sidebar sections completely
        safeSetVisible(sidebarHeader, false);
        safeSetVisible(miniStatsContainer, false);

        // Hide quick action buttons
        safeSetVisible(quickAjukanKredit, false);
        safeSetVisible(quickLelang, false);
        safeSetVisible(quickKopma, false);

        // Hide user info sections
        safeSetVisible(tahunLulusBox, false);

        System.out.println("All student elements hidden successfully");
    }

    private void hideAllMainContent() {
        System.out.println("Hiding ALL main content area...");

        // Hide ALL main content sections - everything!
        safeSetVisible(welcomeSection, false);
        safeSetVisible(mainKpiContainer, false);
        safeSetVisible(quickActionsSection, false);
        safeSetVisible(recentActivitiesSection, false);
        safeSetVisible(infoTab, false);

        // Clear and hide TabPane completely
        if (mainTabPane != null) {
            mainTabPane.getTabs().clear();
            safeSetVisible(mainTabPane, false);
        }

        // Clear table data
        if (riwayatTable != null) {
            riwayatTable.setItems(FXCollections.emptyObservableList());
        }
        if (topTenTable != null) {
            topTenTable.setItems(FXCollections.emptyObservableList());
        }

        // Make main content area completely empty
        if (mainContentScroll != null) {
            // Create completely empty content
            VBox emptyContent = new VBox();
            emptyContent.setStyle("-fx-background-color: transparent;");
            mainContentScroll.setContent(emptyContent);
            System.out.println("Main content area set to completely empty");
        }

        System.out.println("All main content hidden successfully");
    }

    private void setupMinimalAdminSidebar() {
        System.out.println("Setting up minimal admin sidebar...");

        // Show ONLY admin button in sidebar
        safeSetVisible(adminButton, true);

        // Update admin button for better visibility and styling
        if (adminButton != null) {
            String adminType = user instanceof AdminKopma ? "KOPMA" : "Kredit & Lelang";
            adminButton.setText("🔧  Panel Admin " + adminType);

            // Make admin button more prominent
            adminButton.setStyle(
                    "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #1e3a8a, #1d4ed8, #3b82f6); " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 15; " +
                            "-fx-padding: 20 25; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(30, 58, 138, 0.4), 12, 0.4, 0, 6);"
            );

            // Add hover effect
            adminButton.setOnMouseEntered(e -> {
                adminButton.setStyle(
                        "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #1e40af, #2563eb, #60a5fa); " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 16px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-background-radius: 15; " +
                                "-fx-padding: 20 25; " +
                                "-fx-cursor: hand; " +
                                "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.6), 15, 0.5, 0, 8); " +
                                "-fx-scale-x: 1.05; " +
                                "-fx-scale-y: 1.05;"
                );
            });

            adminButton.setOnMouseExited(e -> {
                adminButton.setStyle(
                        "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #1e3a8a, #1d4ed8, #3b82f6); " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 16px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-background-radius: 15; " +
                                "-fx-padding: 20 25; " +
                                "-fx-cursor: hand; " +
                                "-fx-effect: dropshadow(gaussian, rgba(30, 58, 138, 0.4), 12, 0.4, 0, 6);"
                );
            });
        }

        System.out.println("Minimal admin sidebar setup completed");
    }

    private void updateAdminTexts() {
        System.out.println("Updating admin text fields...");

        // Update navigation text
        safeSetText(welcomeNavText, "Dashboard Administrator");

        // Update role text in sidebar
        String roleText = user instanceof AdminKopma ? "Administrator KOPMA" : "Administrator Kredit & Lelang";
        safeSetText(userRole, roleText);

        // Update welcome text in sidebar to be more minimal
        if (welcomeText != null) {
            welcomeText.setText("Admin: " + user.getNama());
            welcomeText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-fill: #1e3a8a;");
        }

        // Update user role styling
        if (userRole != null) {
            userRole.setStyle("-fx-font-size: 12px; -fx-fill: #64748b;");
        }

        System.out.println("Admin text fields updated successfully");
    }

    private void setupFullUserView() {
        System.out.println("Setting up FULL USER view for user: " + user.getNama());

        // Setup table columns for user
        setupTableColumns();
        setupQuickActionButtons();

        // Show all user elements
        safeSetVisible(ajukanKreditButton, true);
        safeSetVisible(lelangButton, true);
        safeSetVisible(kopmaButton, true);
        safeSetVisible(notificationButton, true);
        safeSetVisible(sidebarHeader, true);
        safeSetVisible(miniStatsContainer, true);
        safeSetVisible(welcomeSection, true);
        safeSetVisible(mainKpiContainer, true);
        safeSetVisible(quickActionsSection, true);
        safeSetVisible(recentActivitiesSection, true);
        safeSetVisible(quickAjukanKredit, true);
        safeSetVisible(quickLelang, true);
        safeSetVisible(quickKopma, true);
        safeSetVisible(infoTab, true);
        safeSetVisible(tahunLulusBox, true);

        // Restore tabs for user
        if (mainTabPane != null) {
            safeSetVisible(mainTabPane, true);
            if (!mainTabPane.getTabs().contains(riwayatTab) && riwayatTab != null) {
                mainTabPane.getTabs().add(riwayatTab);
                System.out.println("Restored riwayatTab");
            }
            if (!mainTabPane.getTabs().contains(topTenTab) && topTenTab != null) {
                mainTabPane.getTabs().add(topTenTab);
                System.out.println("Restored topTenTab");
            }
            if (!mainTabPane.getTabs().contains(eventTab) && eventTab != null) {
                mainTabPane.getTabs().add(eventTab);
                System.out.println("Restored eventTab");
            }
        }

        // Hide admin panel for user
        safeSetVisible(adminButton, false);

        // Update welcome texts
        safeSetText(welcomeText, "Selamat Datang, " + user.getNama());
        safeSetText(welcomeNavText, "Selamat Datang, " + user.getNama());
        safeSetText(userRole, "Mahasiswa");

        // Load mahasiswa data
        if (user instanceof Mahasiswa mahasiswa) {
            loadMahasiswaData(mahasiswa);
            safeSetText(tahunLulusText, String.valueOf(mahasiswa.getTahunLulus()));
        }

        // PANGGIL METODE BARU DI SINI
        loadActiveEventsToDashboard();

        // Load leaderboard for user
        loadLeaderboard();

        System.out.println("Full user view setup completed");
    }

    private void loadMahasiswaData(Mahasiswa mahasiswa) {
        try {
            String totalKredit = String.valueOf(mahasiswa.getTotalKredit());

            safeSetText(kreditInfo, totalKredit);

            List<PengajuanKredit> pengajuanList = pengajuanService.getPengajuanByMahasiswa(mahasiswa.getId());
            long jumlahMenunggu = pengajuanList.stream().filter(p -> "Menunggu".equals(p.getStatus())).count();
            long jumlahDisetujui = pengajuanList.stream().filter(p -> "Disetujui".equals(p.getStatus())).count();

            String menungguText = String.valueOf(jumlahMenunggu);
            String disetujuiText = String.valueOf(jumlahDisetujui);

            safeSetText(menungguInfo, menungguText);
            safeSetText(disetujuiInfo, disetujuiText);
            safeSetText(totalKreditInfo, totalKredit + " total pengajuan");
            safeSetText(disetujuiLabel, disetujuiText);
            safeSetText(menungguLabel, menungguText + " menunggu persetujuan");
            safeSetText(miniKreditValue, totalKredit);
            safeSetText(miniMenungguValue, menungguText);

            if (riwayatTable != null) {
                riwayatTable.setItems(FXCollections.observableArrayList(pengajuanList));
                System.out.println("Loaded " + pengajuanList.size() + " items into riwayatTable");
            }
            updateNotificationButton();

            System.out.println("Mahasiswa data loaded successfully for: " + mahasiswa.getNama());

        } catch (Exception e) {
            System.err.println("ERROR in loadMahasiswaData: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadLeaderboard() {
        if (topTenTable == null) {
            System.out.println("topTenTable is null, skipping leaderboard load");
            return;
        }

        List<User> allUsers = userService.getAllUsers();
        List<Mahasiswa> topTenList = allUsers.stream()
                .filter(user -> user instanceof Mahasiswa)
                .map(user -> (Mahasiswa) user)
                .sorted((m1, m2) -> Integer.compare(m2.getTotalKredit(), m1.getTotalKredit()))
                .limit(10)
                .collect(Collectors.toList());

        topTenTable.setItems(FXCollections.observableArrayList(topTenList));
        System.out.println("Loaded " + topTenList.size() + " items into topTenTable");
    }

    private void setupTableColumns() {
        if (rankColumn != null) {
            rankColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(topTenTable.getItems().indexOf(cellData.getValue()) + 1).asObject());
        }
        if (namaColumn != null) {
            namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        }
        if (nimColumn != null) {
            nimColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        }
        if (kreditColumn != null) {
            kreditColumn.setCellValueFactory(new PropertyValueFactory<>("totalKredit"));
        }
        if (riwayatJenisColumn != null) {
            riwayatJenisColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        }
        if (riwayatSubJenisColumn != null) {
            riwayatSubJenisColumn.setCellValueFactory(new PropertyValueFactory<>("subKategori"));
        }
        if (riwayatPoinColumn != null) {
            riwayatPoinColumn.setCellValueFactory(new PropertyValueFactory<>("nilai"));
        }
        if (riwayatTanggalColumn != null) {
            riwayatTanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        }
        if (riwayatStatusColumn != null) {
            riwayatStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        }
        if (riwayatKeteranganColumn != null) {
            riwayatKeteranganColumn.setCellValueFactory(new PropertyValueFactory<>("keterangan"));
        }
        if (riwayatBuktiColumn != null) {
            riwayatBuktiColumn.setCellFactory(p -> createViewFileCell());
        }
        System.out.println("Table columns set up");
    }

    private void setupQuickActionButtons() {
        System.out.println("Setting up quick action buttons for user");
    }

    private TableCell<PengajuanKredit, Void> createViewFileCell() {
        return new TableCell<>() {
            private final Button viewBtn = new Button("Lihat");
            {
                viewBtn.getStyleClass().add("action-btn-info");
                viewBtn.setOnAction(e -> {
                    PengajuanKredit item = getTableView().getItems().get(getIndex());
                    if (item != null) openFile(item.getBukti());
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
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

    private void updateNotificationButton() {
        if ((user instanceof AdminKopma) || (user instanceof AdminKredit)) {
            System.out.println("Skipping notification update for admin");
            return;
        }

        if (user instanceof Mahasiswa && notificationButton != null) {
            List<Notifikasi> unread = notifikasiService.getNotifikasiBelumDibaca(user.getId());
            notificationButton.setText("🔔" + (unread.isEmpty() ? "" : " (" + unread.size() + ")"));
            System.out.println("Updated notification button: " + unread.size() + " unread notifications");
        }
    }

    // ===== EVENT HANDLERS =====

    @FXML
    private void handleNotifications(ActionEvent event) {
        if (!(user instanceof Mahasiswa)) {
            return; // Hanya untuk mahasiswa
        }

        // Ambil SEMUA notifikasi (termasuk yang sudah dibaca)
        List<Notifikasi> allNotifications = notifikasiService.getAllNotifikasiByMahasiswa(user.getId());
        List<Notifikasi> unreadNotifications = allNotifications.stream()
                .filter(n -> "Belum Dibaca".equals(n.getStatus()))
                .collect(Collectors.toList());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/notification_popup.fxml"));
            VBox popupContent = loader.load();
            NotificationPopupController controller = loader.getController();
            controller.initializeData(allNotifications);

            // Buat Stage baru untuk popup
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.UNDECORATED); // Tanpa dekorasi window
            popupStage.initModality(Modality.NONE); // Tidak memblokir window utama
            popupStage.initOwner(SceneManager.getPrimaryStage()); // Milik window utama

            // Atur agar popup tertutup jika fokus hilang
            popupStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    popupStage.hide();
                }
            });

            Scene scene = new Scene(popupContent);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            popupStage.setScene(scene);

            // Posisikan popup di dekat tombol notifikasi
            Button sourceButton = (Button) event.getSource();
            double x = sourceButton.localToScreen(sourceButton.getBoundsInLocal()).getMinX() - 300;
            double y = sourceButton.localToScreen(sourceButton.getBoundsInLocal()).getMaxY() + 5;
            popupStage.setX(x);
            popupStage.setY(y);

            popupStage.show();

            // Tandai notifikasi sebagai "dibaca" setelah popup ditampilkan
            if (!unreadNotifications.isEmpty()) {
                unreadNotifications.forEach(notif -> notifikasiService.tandaiNotifikasiDibaca(notif.getId()));
                // Update tampilan tombol notifikasi setelah popup ditutup
                popupStage.setOnHidden(e -> updateNotificationButton());
            }

        } catch (IOException e) {
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka notifikasi.", e.getMessage());
        }
    }
    // ===== GANTI SELURUH BLOK EVENT HANDLERS INI DI DashboardController.java =====

    @FXML
    private void handleShowEventModal() {
        if (user instanceof Mahasiswa) {
            EventModalController.showEventModal();
        }
    }

    @FXML
    private void handleEventImage1Click() {
        if (user instanceof Mahasiswa) {
            EventModalController.showEventModal();
        }
    }

    @FXML
    private void handleEventImage2Click() {
        if (user instanceof Mahasiswa) {
            EventModalController.showEventModal();
        }
    }

    @FXML
    private void handleEventImage3Click() {
        if (user instanceof Mahasiswa) {
            EventModalController.showEventModal();
        }
    }

    @FXML
    private void handleEventImage4Click() {
        if (user instanceof Mahasiswa) {
            EventModalController.showEventModal();
        }
    }

    @FXML
    private void handleEventImage5Click() {
        if (user instanceof Mahasiswa) {
            EventModalController.showEventModal();
        }
    }

    // Direkomendasikan untuk diubah juga agar konsisten
    @FXML
    private void handleEventDetail1() {
        if (user instanceof Mahasiswa) {
            EventModalController.showEventModal();
        }
    }

    @FXML
    private void handleEventDetail2() {
        if (user instanceof Mahasiswa) {
            EventModalController.showEventModal();
        }
    }

    @FXML
    private void handleEventDetail3() {
        if (user instanceof Mahasiswa) {
            EventModalController.showEventModal();
        }
    }

    @FXML
    private void handleEventDetail4() {
        if (user instanceof Mahasiswa) {
            EventModalController.showEventModal();
        }
    }

    @FXML
    private void handleEventDetail5() {
        if (user instanceof Mahasiswa) {
            EventModalController.showEventModal();
        }
    }


    // ===== NAVIGATION HANDLERS =====

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logout clicked by: " + (user != null ? user.getClass().getSimpleName() : "Unknown"));
        SceneManager.navigateTo("/fxml/login.fxml", 550, 650, null);
    }

    @FXML
    private void handleAjukanKredit(ActionEvent event) {
        if ((user instanceof AdminKopma) || (user instanceof AdminKredit)) {
            System.out.println("Admin tried to access Ajukan Kredit - BLOCKED");
            UIUtils.showAlert(Alert.AlertType.WARNING, "Akses Ditolak", "Fitur Mahasiswa",
                    "Fitur ini hanya tersedia untuk mahasiswa. Gunakan Admin Panel untuk fitur administrasi.");
            return;
        }

        SceneManager.navigateTo("/fxml/input-kredit.fxml", 700, 800, (InputKreditController c) -> {
            c.setMahasiswa((Mahasiswa) user);
            c.initializeData();
        });
    }

    @FXML
    private void handleLelang(ActionEvent event) {
        if ((user instanceof AdminKopma) || (user instanceof AdminKredit)) {
            System.out.println("Admin tried to access Lelang - BLOCKED");
            UIUtils.showAlert(Alert.AlertType.WARNING, "Akses Ditolak", "Fitur Mahasiswa",
                    "Fitur ini hanya tersedia untuk mahasiswa. Gunakan Admin Panel untuk fitur administrasi.");
            return;
        }

        SceneManager.navigateTo("/fxml/lelang.fxml", 1200, 800, (LelangController c) -> {
            c.setMahasiswa((Mahasiswa) user);
            c.initializeData();
        });
    }

    @FXML
    private void handleKopma(ActionEvent event) {
        if ((user instanceof AdminKopma) || (user instanceof AdminKredit)) {
            System.out.println("Admin tried to access KOPMA - BLOCKED");
            UIUtils.showAlert(Alert.AlertType.WARNING, "Akses Ditolak", "Fitur Mahasiswa",
                    "Fitur ini hanya tersedia untuk mahasiswa. Gunakan Admin Panel untuk fitur administrasi.");
            return;
        }

        SceneManager.navigateTo("/fxml/kopma.fxml", 1200, 800, (KopmaController c) -> {
            c.setMahasiswa((Mahasiswa) user);
            c.initializeData();
        });
    }

    @FXML
    private void handleAdmin(ActionEvent event) {
        if (!((user instanceof AdminKopma) || (user instanceof AdminKredit))) {
            System.out.println("Non-admin tried to access Admin Panel - BLOCKED");
            UIUtils.showAlert(Alert.AlertType.WARNING, "Akses Ditolak", "Hanya Administrator",
                    "Hanya administrator yang dapat mengakses panel admin.");
            return;
        }

        System.out.println("Admin accessing Admin Panel: " + user.getClass().getSimpleName());

        try {
            SceneManager.navigateTo("/fxml/admin.fxml", 1400, 900, (AdminController c) -> {
                c.setUser(this.user);
                c.initializeData();
                System.out.println("Admin panel loaded successfully for: " + user.getNama());
            });
        } catch (Exception e) {
            System.err.println("Error loading admin panel: " + e.getMessage());
            e.printStackTrace();
            UIUtils.showAlert(Alert.AlertType.ERROR, "Error", "Gagal Membuka Admin Panel",
                    "Terjadi kesalahan saat membuka panel admin: " + e.getMessage());
        }
    }

    private void loadActiveEventsToDashboard() {
        if (eventListContainer == null) return;

        // 1. Kosongkan kontainer
        eventListContainer.getChildren().clear();

        // 2. Ambil event yang aktif
        List<Event> activeEvents = eventService.getActiveEvents();

        // 3. Jika tidak ada event, tampilkan pesan
        if (activeEvents.isEmpty()) {
            Label noEventLabel = new Label("Saat ini belum ada event mendatang.");
            noEventLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
            eventListContainer.getChildren().add(noEventLabel);
            return;
        }

        // 4. Buat tampilan untuk setiap event
        for (Event event : activeEvents) {
            // Buat ImageView
            ImageView eventImage = new ImageView();
            eventImage.setFitHeight(150);
            eventImage.setFitWidth(200);
            eventImage.setPreserveRatio(true);
            File imageFile = new File(event.getImagePath());
            if (imageFile.exists()) {
                eventImage.setImage(new Image(imageFile.toURI().toString()));
            }

            // Buat Judul dan Deskripsi
            Text title = new Text(event.getTitle());
            title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            Text description = new Text(event.getDescription());
            description.setWrappingWidth(400); // Agar teks tidak terlalu panjang

            // Buat Tombol Detail
            Button detailButton = new Button("📖 Lihat Detail");
            detailButton.getStyleClass().add("action-btn-info");
            detailButton.setOnAction(e -> EventModalController.showEventModal());

            // Gabungkan teks dan tombol dalam VBox
            VBox textContainer = new VBox(10, title, description, detailButton);

            // Gabungkan gambar dan VBox teks dalam HBox
            HBox eventItemBox = new HBox(20, eventImage, textContainer);
            eventItemBox.setPadding(new Insets(10));
            eventItemBox.setStyle("-fx-border-color: #e2e8f0; -fx-border-width: 0 0 1 0;");

            // Tambahkan ke kontainer utama
            eventListContainer.getChildren().add(eventItemBox);
        }
    }
}