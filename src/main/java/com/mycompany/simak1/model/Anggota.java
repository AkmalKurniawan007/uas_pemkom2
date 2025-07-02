/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.simak1.model;

/**
 *
 * @author Akmal Kurniawan
 */
import java.io.Serializable;

public class Anggota implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nama;
    private String noInduk;
    private String jurusan;
    private int tahunMasuk;
    private String jabatan;
    private byte[] fotoProfil;
    private double totalKas; // Field baru untuk menampung total kas

    public Anggota() {}

    // Getters and Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getNoInduk() { return noInduk; }
    public void setNoInduk(String noInduk) { this.noInduk = noInduk; }
    public String getJurusan() { return jurusan; }
    public void setJurusan(String jurusan) { this.jurusan = jurusan; }
    public int getTahunMasuk() { return tahunMasuk; }
    public void setTahunMasuk(int tahunMasuk) { this.tahunMasuk = tahunMasuk; }
    public String getJabatan() { return jabatan; }
    public void setJabatan(String jabatan) { this.jabatan = jabatan; }
    public byte[] getFotoProfil() { return fotoProfil; }
    public void setFotoProfil(byte[] fotoProfil) { this.fotoProfil = fotoProfil; }

    // Getter dan Setter untuk totalKas
    public double getTotalKas() { return totalKas; }
    public void setTotalKas(double totalKas) { this.totalKas = totalKas; }

    // Override toString() untuk tampilan yang lebih baik di ComboBox
    @Override
    public String toString() {
        return nama;
    }
}
