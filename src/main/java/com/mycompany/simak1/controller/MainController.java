package com.mycompany.simak1.controller;

import com.mycompany.simak1.dao.AcaraDao;
import com.mycompany.simak1.dao.AnggotaDao;
import com.mycompany.simak1.dao.KasDao;
import com.mycompany.simak1.model.Acara;
import com.mycompany.simak1.model.Anggota;
import com.mycompany.simak1.model.Kas;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;   
import javafx.scene.Parent;      
import javafx.scene.Scene; 
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;  
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javafx.scene.control.TabPane;

public class MainController {

    // Komponen global
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;
    @FXML private ResourceBundle resources;
    @FXML private TabPane mainTabPane;

    // Komponen FXML untuk Anggota
    @FXML private TableView<Anggota> tableViewAnggota;
    @FXML private TableColumn<Anggota, Integer> idKolom;
    @FXML private TableColumn<Anggota, String> namaKolom;
    @FXML private TableColumn<Anggota, String> noIndukKolom;
    @FXML private TableColumn<Anggota, String> jurusanKolom;
    @FXML private TableColumn<Anggota, Integer> tahunMasukKolom;
    @FXML private TableColumn<Anggota, String> jabatanKolom;
    @FXML private TableColumn<Anggota, Double> totalKasKolom;
    @FXML private TextField fieldNama;
    @FXML private TextField fieldNoInduk;
    @FXML private TextField fieldJurusan;
    @FXML private TextField fieldTahunMasuk;
    @FXML private TextField fieldJabatan;

    // Komponen FXML untuk Acara
    @FXML private TableView<Acara> tableViewAcara;
    @FXML private TableColumn<Acara, Integer> idAcaraKolom;
    @FXML private TableColumn<Acara, String> namaAcaraKolom;
    @FXML private TableColumn<Acara, LocalDate> tanggalKolom;
    @FXML private TableColumn<Acara, String> lokasiKolom;
    @FXML private TableColumn<Acara, Double> budgetKolom;
    @FXML private TextField fieldNamaAcara;
    @FXML private DatePicker datePickerTanggal;
    @FXML private TextField fieldLokasi;
    @FXML private TextField fieldBudget;
    @FXML private TextArea areaDeskripsi;

    // Komponen FXML untuk Kas
    @FXML private Tab kasTab;
    @FXML private TableView<Kas> tableViewKas;
    @FXML private TableColumn<Kas, LocalDate> tanggalKasKolom;
    @FXML private TableColumn<Kas, String> jenisKasKolom;
    @FXML private TableColumn<Kas, Double> jumlahKasKolom;
    @FXML private TableColumn<Kas, String> anggotaKasKolom;
    @FXML private TableColumn<Kas, String> deskripsiKasKolom;
    @FXML private ComboBox<String> comboJenisKas;
    @FXML private DatePicker datePickerKas;
    @FXML private TextField fieldJumlahKas;
    @FXML private Label labelAnggotaKas;
    @FXML private ComboBox<Anggota> comboAnggotaKas;
    @FXML private Label labelAcaraKas;
    @FXML private ComboBox<Acara> comboAcaraKas;
    @FXML private TextArea areaDeskripsiKas;
    @FXML private Label labelTotalKas;

    // DAO dan Data Lists
    private AnggotaDao anggotaDao;
    private ObservableList<Anggota> anggotaData;
    private AcaraDao acaraDao;
    private ObservableList<Acara> acaraData;
    private KasDao kasDao;
    private ObservableList<Kas> kasData;
    
    private NumberFormat currencyFormat;
    

    @FXML
    public void initialize() {
        this.currencyFormat = NumberFormat.getCurrencyInstance(resources.getLocale());

        progressBar.setVisible(false);
        

        anggotaDao = new AnggotaDao();
        anggotaData = FXCollections.observableArrayList();
        setupTableAnggota();
        loadAnggotaData();
        tableViewAnggota.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> showAnggotaDetails(newSelection));

        acaraDao = new AcaraDao();
        acaraData = FXCollections.observableArrayList();
        setupTableAcara();
        loadAcaraData();
        tableViewAcara.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> showAcaraDetails(newSelection));
        
        kasDao = new KasDao();
        kasData = FXCollections.observableArrayList();
        setupTableKas();
        setupFormKas();
        loadAnggotaToCombo();
        loadAcaraToCombo();
        loadKasData();
        
        // Listener untuk me-refresh data saat tab Kas dipilih
        if (kasTab != null) {
            kasTab.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    refreshComboBoxes();
                }
            });
        }
    }
    
    // ===============================================
    // TAMBAHAN: BLOK MANAJEMEN BAHASA
    // ===============================================
    @FXML private void handleLanguageIndonesian(ActionEvent event) { loadLanguage("id"); }
    @FXML private void handleLanguageEnglish(ActionEvent event) { loadLanguage("en"); }
    @FXML private void handleLanguageGerman(ActionEvent event) { loadLanguage("de"); }
    @FXML private void handleLanguageJapanese(ActionEvent event) { loadLanguage("ja"); }

    private void loadLanguage(String langCode) {
        try {
            Locale locale = new Locale(langCode);
            // Pastikan path ke resource bundle sudah benar
            ResourceBundle bundle = ResourceBundle.getBundle("com/mycompany/simak1/i18n/messages", locale);

            // Ganti path FXML jika berbeda
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/simak1/view/MainView.fxml"), bundle);
            Parent root = loader.load();
            
            // Dapatkan stage saat ini dan ganti scene-nya
            Stage stage = (Stage) mainTabPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "alert.gagal.title", "Gagal memuat tampilan."); // Tampilkan pesan dasar jika bundle gagal
        }
    }

    // ===============================================
    //               MANAJEMEN ANGGOTA
    // ===============================================
    private void setupTableAnggota() {
        idKolom.setCellValueFactory(new PropertyValueFactory<>("id"));
        namaKolom.setCellValueFactory(new PropertyValueFactory<>("nama"));
        noIndukKolom.setCellValueFactory(new PropertyValueFactory<>("noInduk"));
        jurusanKolom.setCellValueFactory(new PropertyValueFactory<>("jurusan"));
        tahunMasukKolom.setCellValueFactory(new PropertyValueFactory<>("tahunMasuk"));
        jabatanKolom.setCellValueFactory(new PropertyValueFactory<>("jabatan"));
        totalKasKolom.setCellValueFactory(new PropertyValueFactory<>("totalKas"));

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        totalKasKolom.setCellFactory(tc -> new TableCell<Anggota, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                setText(empty || total == null ? "" : currencyFormat.format(total));
            }
        });
    }

    private void loadAnggotaData() {
        try {
            anggotaData.setAll(anggotaDao.getAll());
            tableViewAnggota.setItems(anggotaData);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal Memuat", "Gagal memuat data anggota.");
        }
    }

    private void showAnggotaDetails(Anggota anggota) {
        if (anggota != null) {
            fieldNama.setText(anggota.getNama());
            fieldNoInduk.setText(anggota.getNoInduk());
            fieldJurusan.setText(anggota.getJurusan());
            fieldTahunMasuk.setText(Integer.toString(anggota.getTahunMasuk()));
            fieldJabatan.setText(anggota.getJabatan());
        } else {
            fieldNama.clear();
            fieldNoInduk.clear();
            fieldJurusan.clear();
            fieldTahunMasuk.clear();
            fieldJabatan.clear();
        }
    }

    @FXML
    private void handleTambahAnggota() {
        try {
            Anggota baru = new Anggota();
            baru.setNama(fieldNama.getText());
            baru.setNoInduk(fieldNoInduk.getText());
            baru.setJurusan(fieldJurusan.getText());
            baru.setTahunMasuk(Integer.parseInt(fieldTahunMasuk.getText()));
            baru.setJabatan(fieldJabatan.getText());
            anggotaDao.save(baru);
            loadAnggotaData();
            showAnggotaDetails(null);
            refreshComboBoxes();
        } catch (NumberFormatException | SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan anggota baru. Periksa kembali data Anda.");
        }
    }

    @FXML
    private void handleEditAnggota() {
        Anggota terpilih = tableViewAnggota.getSelectionModel().getSelectedItem();
        if (terpilih != null) {
            try {
                terpilih.setNama(fieldNama.getText());
                terpilih.setNoInduk(fieldNoInduk.getText());
                terpilih.setJurusan(fieldJurusan.getText());
                terpilih.setTahunMasuk(Integer.parseInt(fieldTahunMasuk.getText()));
                terpilih.setJabatan(fieldJabatan.getText());
                anggotaDao.update(terpilih);
                loadAnggotaData();
                refreshComboBoxes();
            } catch (NumberFormatException | SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memperbarui anggota.");
            }
        }
    }
    
     @FXML
    private void handleExit(ActionEvent event) {
        // Mengambil stage (jendela) saat ini dan menutupnya
        Stage stage = (Stage) mainTabPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleHapusAnggota() {
        Anggota terpilih = tableViewAnggota.getSelectionModel().getSelectedItem();
        if (terpilih != null) {
            try {
                anggotaDao.delete(terpilih);
                loadAnggotaData();
                refreshComboBoxes();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus anggota.");
            }
        }
    }

    // ===============================================
    //               MANAJEMEN ACARA
    // ===============================================
    private void setupTableAcara() {
        idAcaraKolom.setCellValueFactory(new PropertyValueFactory<>("id"));
        namaAcaraKolom.setCellValueFactory(new PropertyValueFactory<>("namaAcara"));
        tanggalKolom.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        lokasiKolom.setCellValueFactory(new PropertyValueFactory<>("lokasi"));
        budgetKolom.setCellValueFactory(new PropertyValueFactory<>("budget"));

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        budgetKolom.setCellFactory(tc -> new TableCell<Acara, Double>() {
            @Override
            protected void updateItem(Double budget, boolean empty) {
                super.updateItem(budget, empty);
                setText(empty || budget == null ? "" : currencyFormat.format(budget));
            }
        });
    }

    private void loadAcaraData() {
        try {
            acaraData.setAll(acaraDao.getAll());
            tableViewAcara.setItems(acaraData);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal Memuat", "Gagal memuat data acara.");
        }
    }

    private void showAcaraDetails(Acara acara) {
        if (acara != null) {
            fieldNamaAcara.setText(acara.getNamaAcara());
            datePickerTanggal.setValue(acara.getTanggal());
            fieldLokasi.setText(acara.getLokasi());
            fieldBudget.setText(String.format("%.0f", acara.getBudget()));
            areaDeskripsi.setText(acara.getDeskripsi());
        } else {
            fieldNamaAcara.clear();
            datePickerTanggal.setValue(null);
            fieldLokasi.clear();
            fieldBudget.clear();
            areaDeskripsi.clear();
        }
    }

    @FXML
    private void handleTambahAcara() {
        if (datePickerTanggal.getValue() != null && !fieldNamaAcara.getText().isEmpty()) {
            try {
                Acara baru = new Acara();
                baru.setNamaAcara(fieldNamaAcara.getText());
                baru.setTanggal(datePickerTanggal.getValue());
                baru.setLokasi(fieldLokasi.getText());
                baru.setDeskripsi(areaDeskripsi.getText());
                String budgetText = fieldBudget.getText();
                double budget = 0.0;
                if (budgetText != null && !budgetText.trim().isEmpty()) {
                    budget = Double.parseDouble(budgetText);
                }
                baru.setBudget(budget);
                acaraDao.save(baru);
                loadAcaraData();
                showAcaraDetails(null);
                refreshComboBoxes();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan acara baru.");
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Input Salah", "Budget harus berupa angka yang valid.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Valid", "Nama dan Tanggal acara harus diisi.");
        }
    }

    @FXML
    private void handleEditAcara() {
        Acara terpilih = tableViewAcara.getSelectionModel().getSelectedItem();
        if (terpilih != null) {
             if (datePickerTanggal.getValue() != null && !fieldNamaAcara.getText().isEmpty()) {
                try {
                    terpilih.setNamaAcara(fieldNamaAcara.getText());
                    terpilih.setTanggal(datePickerTanggal.getValue());
                    terpilih.setLokasi(fieldLokasi.getText());
                    terpilih.setDeskripsi(areaDeskripsi.getText());
                    String budgetText = fieldBudget.getText();
                    double budget = 0.0;
                    if (budgetText != null && !budgetText.trim().isEmpty()) {
                        budget = Double.parseDouble(budgetText);
                    }
                    terpilih.setBudget(budget);
                    acaraDao.update(terpilih);
                    loadAcaraData();
                    refreshComboBoxes();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memperbarui acara.");
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.WARNING, "Input Salah", "Budget harus berupa angka.");
                }
            }
        }
    }

    @FXML
    private void handleHapusAcara() {
        Acara terpilih = tableViewAcara.getSelectionModel().getSelectedItem();
        if (terpilih != null) {
            try {
                acaraDao.delete(terpilih);
                loadAcaraData();
                refreshComboBoxes();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus acara.");
            }
        }
    }

    // ===============================================
    //               MANAJEMEN KAS
    // ===============================================
    private void setupTableKas() {
        tanggalKasKolom.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        jenisKasKolom.setCellValueFactory(new PropertyValueFactory<>("jenis"));
        jumlahKasKolom.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        anggotaKasKolom.setCellValueFactory(cellData -> {
            Kas kas = cellData.getValue();
            if ("Pemasukan".equals(kas.getJenis())) {
                return new javafx.beans.property.SimpleStringProperty(kas.getNamaAnggota() != null ? kas.getNamaAnggota() : "Umum/Lainnya");
            } else if ("Pengeluaran".equals(kas.getJenis())) {
                return new javafx.beans.property.SimpleStringProperty(kas.getNamaAcara() != null ? kas.getNamaAcara() : "Lainnya");
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        deskripsiKasKolom.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        jumlahKasKolom.setCellFactory(tc -> new TableCell<Kas, Double>() {
            @Override
            protected void updateItem(Double jumlah, boolean empty) {
                super.updateItem(jumlah, empty);
                setText(empty || jumlah == null ? "" : currencyFormat.format(jumlah));
            }
        });
    }
    
    private void setupFormKas() {
        comboJenisKas.getItems().addAll("Pemasukan", "Pengeluaran");
        datePickerKas.setValue(LocalDate.now());

        comboAnggotaKas.setConverter(new StringConverter<Anggota>() {
            @Override public String toString(Anggota a) { return a == null ? "Umum/Lainnya" : a.getNama(); }
            @Override public Anggota fromString(String s) { return null; }
        });
        
        comboAcaraKas.setConverter(new StringConverter<Acara>() {
            @Override public String toString(Acara a) { return a == null ? "Lainnya" : a.getNamaAcara(); }
            @Override public Acara fromString(String s) { return null; }
        });
        
        comboAnggotaKas.getItems().add(null); 
        comboAcaraKas.getItems().add(null);

        comboJenisKas.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isPemasukan = "Pemasukan".equals(newVal);
            labelAnggotaKas.setVisible(isPemasukan);
            comboAnggotaKas.setVisible(isPemasukan);
            labelAcaraKas.setVisible(!isPemasukan);
            comboAcaraKas.setVisible(!isPemasukan);
        });
        
        comboJenisKas.setValue("Pemasukan");
    }
    
    private void loadKasData() {
        try {
            kasData.setAll(kasDao.getAll());
            tableViewKas.setItems(kasData);
            updateTotalKas();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memuat data kas.");
            e.printStackTrace();
        }
    }
    
    private void loadAnggotaToCombo() {
        try {
            List<Anggota> anggotaList = anggotaDao.getAll();
            comboAnggotaKas.getItems().addAll(anggotaList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memuat daftar anggota.");
        }
    }

    private void loadAcaraToCombo() {
        try {
            comboAcaraKas.getItems().addAll(acaraDao.getAll());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memuat daftar acara.");
        }
    }

    private void updateTotalKas() {
        double total = 0;
        for (Kas kas : kasData) {
            total += "Pemasukan".equals(kas.getJenis()) ? kas.getJumlah() : -kas.getJumlah();
        }
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        labelTotalKas.setText(currencyFormat.format(total));
    }
    
    @FXML
    private void handleTambahKas() {
        if (comboJenisKas.getValue() == null || datePickerKas.getValue() == null || fieldJumlahKas.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Jenis, Tanggal, dan Jumlah harus diisi.");
            return;
        }
        
        try {
            Kas baru = new Kas();
            baru.setJenis(comboJenisKas.getValue());
            baru.setTanggal(datePickerKas.getValue());
            baru.setJumlah(Double.parseDouble(fieldJumlahKas.getText()));
            baru.setDeskripsi(areaDeskripsiKas.getText());
            
            if ("Pemasukan".equals(baru.getJenis())) {
                Anggota anggotaTerpilih = comboAnggotaKas.getValue();
                if (anggotaTerpilih != null) baru.setIdAnggota(anggotaTerpilih.getId());
            } else { // Pengeluaran
                Acara acaraTerpilih = comboAcaraKas.getValue();
                if (acaraTerpilih != null) {
                    baru.setIdAcara(acaraTerpilih.getId());
                    acaraTerpilih.setBudget(acaraTerpilih.getBudget() - baru.getJumlah());
                    acaraDao.update(acaraTerpilih);
                    loadAcaraData();
                }
            }

            kasDao.save(baru);
            loadKasData(); 
            loadAnggotaData(); // REFRESH TABEL ANGGOTA
            
            fieldJumlahKas.clear();
            areaDeskripsiKas.clear();
            comboAnggotaKas.getSelectionModel().clearSelection();
            comboAcaraKas.getSelectionModel().clearSelection();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Salah", "Jumlah harus berupa angka.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan transaksi kas.");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleHapusKas() {
        Kas terpilih = tableViewKas.getSelectionModel().getSelectedItem();
        if (terpilih != null) {
            try {
                kasDao.delete(terpilih);
                loadKasData();
                loadAnggotaData(); // REFRESH TABEL ANGGOTA
            } catch (SQLException e) {
                 showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus transaksi.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Tidak Ada Pilihan", "Pilih transaksi yang ingin dihapus.");
        }
    }

    @FXML
    private void handleRefreshKasForm() {
        refreshComboBoxes();
        showAlert(Alert.AlertType.INFORMATION, "Refresh", "Data anggota dan acara telah diperbarui.");
    }
    
    private void refreshComboBoxes() {
        Anggota selectedAnggota = comboAnggotaKas.getSelectionModel().getSelectedItem();
        Acara selectedAcara = comboAcaraKas.getSelectionModel().getSelectedItem();
    
        comboAnggotaKas.getItems().clear();
        comboAcaraKas.getItems().clear();
    
        comboAnggotaKas.getItems().add(null); 
        comboAcaraKas.getItems().add(null);
    
        loadAnggotaToCombo();
        loadAcaraToCombo();
    
        comboAnggotaKas.getSelectionModel().select(selectedAnggota);
        comboAcaraKas.getSelectionModel().select(selectedAcara);
    }
    
    // ===============================================
    //               FITUR LAINNYA & HELPERS
    // ===============================================
    @FXML
    private void handleGenerateReport() {
        // ... (Kode tidak diubah)
    }

    @FXML
    private void handleExportEventTemplate() {
        // ... (Kode tidak diubah)
    }

    @FXML
    private void handleImportEventTemplate() {
       // ... (Kode tidak diubah)
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
