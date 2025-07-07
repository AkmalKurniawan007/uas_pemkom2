package com.mycompany.simak1.controller;

import com.mycompany.simak1.dao.AcaraDao;
import com.mycompany.simak1.dao.AnggotaDao;
import com.mycompany.simak1.dao.KasDao;
import com.mycompany.simak1.model.Acara;
import com.mycompany.simak1.model.Anggota;
import com.mycompany.simak1.model.Kas;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;


public class MainController {

    // (Deklarasi FXML dan properti lainnya tetap sama)
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;
    @FXML private ResourceBundle resources;
    @FXML private TabPane mainTabPane;
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
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
        statusLabel.textProperty().unbind();
        statusLabel.setText(resources.getString("statusbar.ready"));

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

        if (kasTab != null) {
            kasTab.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    refreshComboBoxes();
                }
            });
        }
    }

    private <T> void executeTask(Supplier<List<T>> dataSupplier, ObservableList<T> dataList, String taskName) {
        Task<List<T>> task = new Task<>() {
            @Override
            protected List<T> call() throws Exception {
                updateMessage("Memuat data " + taskName + "...");
                List<T> result = dataSupplier.get();
                updateMessage("Data " + taskName + " berhasil dimuat.");
                return result;
            }
        };

        task.setOnSucceeded(e -> {
            dataList.setAll(task.getValue());
            statusLabel.textProperty().unbind();
            statusLabel.setText(task.getMessage());
            progressBar.setVisible(false);
        });

        task.setOnFailed(e -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText("Gagal memuat data " + taskName + ".");
            showAlert(Alert.AlertType.ERROR, "Gagal", "Terjadi kesalahan: " + task.getException().getMessage());
            progressBar.setVisible(false);
        });

        progressBar.setVisible(true);
        progressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());

        new Thread(task).start();
    }
    
    // (Metode manajemen Anggota, Acara, dan Kas tetap sama...)
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
        executeTask(() -> {
            try {
                return anggotaDao.getAll();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, anggotaData, "Anggota");
        tableViewAnggota.setItems(anggotaData);
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
         executeTask(() -> {
            try {
                return acaraDao.getAll();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, acaraData, "Acara");
        tableViewAcara.setItems(acaraData);
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
        executeTask(() -> {
            try {
                return kasDao.getAll();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, kasData, "Transaksi Kas");
        tableViewKas.setItems(kasData);
        updateTotalKas();
    }
    
    // MODIFIKASI: Logika pengisian ComboBox diperbaiki untuk menghindari error ambiguitas
    private void loadAnggotaToCombo() {
        try {
            List<Anggota> anggotaList = anggotaDao.getAll();
            comboAnggotaKas.getItems().clear(); // Hapus item lama
            comboAnggotaKas.getItems().add(null); // Tambahkan opsi null untuk "Umum"
            comboAnggotaKas.getItems().addAll(anggotaList); // Tambahkan semua anggota dari database
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memuat daftar anggota.");
        }
    }

    // MODIFIKASI: Logika pengisian ComboBox diperbaiki untuk menghindari error ambiguitas
    private void loadAcaraToCombo() {
        try {
            List<Acara> acaraList = acaraDao.getAll();
            comboAcaraKas.getItems().clear(); // Hapus item lama
            comboAcaraKas.getItems().add(null); // Tambahkan opsi null untuk "Lainnya"
            comboAcaraKas.getItems().addAll(acaraList); // Tambahkan semua acara dari database
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
            } else { 
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
            loadAnggotaData(); 
            
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
                loadAnggotaData();
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
    
        loadAnggotaToCombo();
        loadAcaraToCombo();
    
        comboAnggotaKas.getSelectionModel().select(selectedAnggota);
        comboAcaraKas.getSelectionModel().select(selectedAcara);
    }
    
    // ===============================================
    // FITUR LAINNYA & HELPERS
    // ===============================================

    @FXML
    private void handleGenerateReport() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Enkripsi Laporan");
        dialog.setHeaderText("Masukkan Kata Sandi untuk Melindungi Laporan PDF");
        dialog.setContentText("Kata Sandi:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isEmpty()){
            String userPassword = result.get();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Simpan Laporan Terenkripsi");
            fileChooser.setInitialFileName("Laporan_Lengkap_Terenkripsi.pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(mainTabPane.getScene().getWindow());

            if (file != null) {
                Task<Void> reportTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        updateMessage("Membuat laporan PDF...");
                        try {
                            WriterProperties writerProperties = new WriterProperties()
                                .setStandardEncryption(
                                    userPassword.getBytes(),
                                    userPassword.getBytes(),
                                    EncryptionConstants.ALLOW_PRINTING,
                                    EncryptionConstants.ENCRYPTION_AES_256);

                            PdfWriter writer = new PdfWriter(file.getAbsolutePath(), writerProperties);
                            PdfDocument pdf = new PdfDocument(writer);
                            Document document = new Document(pdf);
                            
                            NumberFormat rpFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                            
                            document.add(new Paragraph("Laporan Tahunan Komprehensif").setBold().setFontSize(18).setUnderline());
                            document.add(new Paragraph("Dihasilkan pada: " + LocalDate.now()));
                            document.add(new Paragraph("Dokumen ini dilindungi oleh kata sandi.\n\n"));

                            document.add(new Paragraph("Data Anggota").setBold().setFontSize(14));
                            float[] anggotaColumnWidths = {1, 4, 3, 3, 2, 3};
                            Table anggotaTable = new Table(UnitValue.createPercentArray(anggotaColumnWidths));
                            anggotaTable.setWidth(UnitValue.createPercentValue(100));
                            anggotaTable.addHeaderCell(new Cell().add(new Paragraph("ID")));
                            anggotaTable.addHeaderCell(new Cell().add(new Paragraph("Nama")));
                            anggotaTable.addHeaderCell(new Cell().add(new Paragraph("No. Induk")));
                            anggotaTable.addHeaderCell(new Cell().add(new Paragraph("Jurusan")));
                            anggotaTable.addHeaderCell(new Cell().add(new Paragraph("Masuk")));
                            anggotaTable.addHeaderCell(new Cell().add(new Paragraph("Jabatan")));

                            for(Anggota anggota : tableViewAnggota.getItems()) {
                                anggotaTable.addCell(new Cell().add(new Paragraph(String.valueOf(anggota.getId()))));
                                anggotaTable.addCell(new Cell().add(new Paragraph(anggota.getNama())));
                                anggotaTable.addCell(new Cell().add(new Paragraph(anggota.getNoInduk())));
                                anggotaTable.addCell(new Cell().add(new Paragraph(anggota.getJurusan())));
                                anggotaTable.addCell(new Cell().add(new Paragraph(String.valueOf(anggota.getTahunMasuk()))));
                                anggotaTable.addCell(new Cell().add(new Paragraph(anggota.getJabatan())));
                            }
                            document.add(anggotaTable);

                            document.add(new Paragraph("\nData Acara").setBold().setFontSize(14));
                            float[] acaraColumnWidths = {1, 4, 3, 4, 3};
                            Table acaraTable = new Table(UnitValue.createPercentArray(acaraColumnWidths));
                            acaraTable.setWidth(UnitValue.createPercentValue(100));
                            acaraTable.addHeaderCell(new Cell().add(new Paragraph("ID")));
                            acaraTable.addHeaderCell(new Cell().add(new Paragraph("Nama Acara")));
                            acaraTable.addHeaderCell(new Cell().add(new Paragraph("Tanggal")));
                            acaraTable.addHeaderCell(new Cell().add(new Paragraph("Lokasi")));
                            acaraTable.addHeaderCell(new Cell().add(new Paragraph("Budget")));

                            for(Acara acara : tableViewAcara.getItems()) {
                                acaraTable.addCell(new Cell().add(new Paragraph(String.valueOf(acara.getId()))));
                                acaraTable.addCell(new Cell().add(new Paragraph(acara.getNamaAcara())));
                                acaraTable.addCell(new Cell().add(new Paragraph(acara.getTanggal().toString())));
                                acaraTable.addCell(new Cell().add(new Paragraph(acara.getLokasi())));
                                acaraTable.addCell(new Cell().add(new Paragraph(rpFormat.format(acara.getBudget()))));
                            }
                            document.add(acaraTable);

                            document.add(new Paragraph("\nData Transaksi Kas").setBold().setFontSize(14));
                            float[] kasColumnWidths = {3, 2, 3, 4, 5};
                            Table kasTable = new Table(UnitValue.createPercentArray(kasColumnWidths));
                            kasTable.setWidth(UnitValue.createPercentValue(100));
                            kasTable.addHeaderCell(new Cell().add(new Paragraph("Tanggal")));
                            kasTable.addHeaderCell(new Cell().add(new Paragraph("Jenis")));
                            kasTable.addHeaderCell(new Cell().add(new Paragraph("Jumlah")));
                            kasTable.addHeaderCell(new Cell().add(new Paragraph("Sumber/Tujuan")));
                            kasTable.addHeaderCell(new Cell().add(new Paragraph("Deskripsi")));

                            for(Kas kas : tableViewKas.getItems()) {
                                kasTable.addCell(new Cell().add(new Paragraph(kas.getTanggal().toString())));
                                kasTable.addCell(new Cell().add(new Paragraph(kas.getJenis())));
                                kasTable.addCell(new Cell().add(new Paragraph(rpFormat.format(kas.getJumlah()))));
                                String sumberTujuan = "";
                                if ("Pemasukan".equals(kas.getJenis())) {
                                    sumberTujuan = kas.getNamaAnggota() != null ? kas.getNamaAnggota() : "Umum/Lainnya";
                                } else {
                                    sumberTujuan = kas.getNamaAcara() != null ? kas.getNamaAcara() : "Lainnya";
                                }
                                kasTable.addCell(new Cell().add(new Paragraph(sumberTujuan)));
                                kasTable.addCell(new Cell().add(new Paragraph(kas.getDeskripsi())));
                            }
                            document.add(kasTable);

                            document.close();
                        } catch (IOException e) {
                            throw new RuntimeException("Gagal menulis file: " + e.getMessage(), e);
                        }
                        updateMessage("Laporan berhasil dibuat.");
                        return null;
                    }
                };
                
                reportTask.setOnSucceeded(e -> {
                    progressBar.setVisible(false);
                    statusLabel.textProperty().unbind();
                    statusLabel.setText("Laporan berhasil dibuat.");
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Laporan terenkripsi berhasil dibuat di:\n" + file.getAbsolutePath());
                });

                reportTask.setOnFailed(e -> {
                    progressBar.setVisible(false);
                    statusLabel.textProperty().unbind();
                    statusLabel.setText("Gagal membuat laporan.");
                    showAlert(Alert.AlertType.ERROR, "Gagal", "Terjadi kesalahan saat membuat laporan: " + reportTask.getException().getMessage());
                });

                progressBar.setVisible(true);
                progressBar.progressProperty().bind(reportTask.progressProperty());
                statusLabel.textProperty().bind(reportTask.messageProperty());

                new Thread(reportTask).start();
            }
        } else {
             showAlert(Alert.AlertType.WARNING, "Dibatalkan", "Pembuatan laporan dibatalkan karena tidak ada kata sandi yang dimasukkan.");
        }
    }
    
    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) mainTabPane.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleExportEventTemplate() {
        Acara selectedAcara = tableViewAcara.getSelectionModel().getSelectedItem();
        if (selectedAcara == null) {
            showAlert(Alert.AlertType.WARNING, "Tidak Ada Pilihan", "Pilih sebuah acara dari tabel untuk diekspor sebagai template.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Template Acara");
        fileChooser.setInitialFileName("template-" + selectedAcara.getNamaAcara().replaceAll("\\s+", "-") + ".properties");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Template Acara (*.properties)", "*.properties"));
        File file = fileChooser.showSaveDialog(mainTabPane.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("namaAcara=" + selectedAcara.getNamaAcara());
                writer.println("lokasi=" + selectedAcara.getLokasi());
                writer.println("deskripsi=" + selectedAcara.getDeskripsi().replace("\n", "\\n"));
                
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Template acara berhasil disimpan di:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Gagal Menyimpan", "Terjadi kesalahan saat menyimpan file template.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleImportEventTemplate() {
       FileChooser fileChooser = new FileChooser();
       fileChooser.setTitle("Buka Template Acara");
       fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Template Acara (*.properties)", "*.properties"));
       File file = fileChooser.showOpenDialog(mainTabPane.getScene().getWindow());

       if (file != null) {
           Properties props = new Properties();
           try (FileInputStream fis = new FileInputStream(file)) {
               props.load(fis);
               
               fieldNamaAcara.setText(props.getProperty("namaAcara", ""));
               fieldLokasi.setText(props.getProperty("lokasi", ""));
               areaDeskripsi.setText(props.getProperty("deskripsi", "").replace("\\n", "\n"));

               datePickerTanggal.setValue(null);
               fieldBudget.clear();

               showAlert(Alert.AlertType.INFORMATION, "Sukses", "Template berhasil dimuat. Silakan isi tanggal dan budget.");

           } catch (IOException e) {
               showAlert(Alert.AlertType.ERROR, "Gagal Membaca", "Terjadi kesalahan saat membaca file template.");
               e.printStackTrace();
           }
       }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML private void handleLanguageIndonesian(ActionEvent event) { loadLanguage("id"); }
    @FXML private void handleLanguageEnglish(ActionEvent event) { loadLanguage("en"); }
    @FXML private void handleLanguageGerman(ActionEvent event) { loadLanguage("de"); }
    @FXML private void handleLanguageJapanese(ActionEvent event) { loadLanguage("ja"); }

    private void loadLanguage(String langCode) {
        try {
            Locale locale = new Locale(langCode);
            ResourceBundle bundle = ResourceBundle.getBundle("com/mycompany/simak1/i18n/messages", locale);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/simak1/view/MainView.fxml"), bundle);
            Parent root = loader.load();

            Stage stage = (Stage) mainTabPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gagal Memuat Tampilan", "Gagal memuat tampilan untuk bahasa yang dipilih.");
        }
    }
}