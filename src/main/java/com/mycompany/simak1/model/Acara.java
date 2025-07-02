package com.mycompany.simak1.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Acara implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String namaAcara;
    private LocalDate tanggal;
    private String lokasi;
    private String deskripsi;
    private double budget; // Field baru

    // Getters and Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNamaAcara() { return namaAcara; }
    public void setNamaAcara(String namaAcara) { this.namaAcara = namaAcara; }
    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    // Getter dan Setter untuk budget
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
}
