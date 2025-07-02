/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.simak1.model;

/**
 *
 * @author Akmal Kurniawan
 */

import java.time.LocalDate;

public class Kas {
    private int id;
    private Integer idAnggota;
    private String namaAnggota;
    private Integer idAcara; // Field baru
    private String namaAcara; // Field baru untuk tampilan
    private LocalDate tanggal;
    private String jenis;
    private double jumlah;
    private String deskripsi;

    // Getters and Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getIdAnggota() { return idAnggota; }
    public void setIdAnggota(Integer idAnggota) { this.idAnggota = idAnggota; }

    public String getNamaAnggota() { return namaAnggota; }
    public void setNamaAnggota(String namaAnggota) { this.namaAnggota = namaAnggota; }
    
    public Integer getIdAcara() { return idAcara; }
    public void setIdAcara(Integer idAcara) { this.idAcara = idAcara; }

    public String getNamaAcara() { return namaAcara; }
    public void setNamaAcara(String namaAcara) { this.namaAcara = namaAcara; }

    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }

    public double getJumlah() { return jumlah; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
}

