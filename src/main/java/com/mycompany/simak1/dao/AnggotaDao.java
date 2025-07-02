/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.simak1.dao;

import com.mycompany.simak1.model.Anggota;
import com.mycompany.simak1.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementasi konkret dari Dao generik untuk model Anggota.
 */
public class AnggotaDao implements Dao<Anggota> {
    
    private Connection conn = DatabaseUtil.getConnection();

    @Override
    public Optional<Anggota> get(int id) throws SQLException {
        String sql = "SELECT * FROM anggota WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Anggota anggota = new Anggota();
                anggota.setId(rs.getInt("id"));
                anggota.setNama(rs.getString("nama"));
                anggota.setNoInduk(rs.getString("no_induk"));
                anggota.setJurusan(rs.getString("jurusan"));
                anggota.setTahunMasuk(rs.getInt("tahun_masuk"));
                anggota.setJabatan(rs.getString("jabatan"));
                anggota.setFotoProfil(rs.getBytes("foto_profil"));
                return Optional.of(anggota);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Anggota> getAll() throws SQLException {
        List<Anggota> anggotaList = new ArrayList<>();
        // Query SQL diubah untuk MENGHITUNG TOTAL KAS per anggota
        String sql = "SELECT a.*, COALESCE(SUM(k.jumlah), 0) as total_kas " +
                     "FROM anggota a " +
                     "LEFT JOIN kas k ON a.id = k.id_anggota AND k.jenis = 'Pemasukan' " +
                     "GROUP BY a.id";
                     
        try (Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Anggota anggota = new Anggota();
                anggota.setId(rs.getInt("id"));
                anggota.setNama(rs.getString("nama"));
                anggota.setNoInduk(rs.getString("no_induk"));
                anggota.setJurusan(rs.getString("jurusan"));
                anggota.setTahunMasuk(rs.getInt("tahun_masuk"));
                anggota.setJabatan(rs.getString("jabatan"));
                anggota.setFotoProfil(rs.getBytes("foto_profil"));
                anggota.setTotalKas(rs.getDouble("total_kas")); // Mengambil total kas
                anggotaList.add(anggota);
            }
        }
        return anggotaList;
    }

    @Override
    public void save(Anggota anggota) throws SQLException {
        String sql = "INSERT INTO anggota(nama, no_induk, jurusan, tahun_masuk, jabatan, foto_profil) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, anggota.getNama());
            pstmt.setString(2, anggota.getNoInduk());
            pstmt.setString(3, anggota.getJurusan());
            pstmt.setInt(4, anggota.getTahunMasuk());
            pstmt.setString(5, anggota.getJabatan());
            pstmt.setBytes(6, anggota.getFotoProfil());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void update(Anggota anggota) throws SQLException {
        String sql = "UPDATE anggota SET nama = ?, no_induk = ?, jurusan = ?, tahun_masuk = ?, jabatan = ?, foto_profil = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, anggota.getNama());
            pstmt.setString(2, anggota.getNoInduk());
            pstmt.setString(3, anggota.getJurusan());
            pstmt.setInt(4, anggota.getTahunMasuk());
            pstmt.setString(5, anggota.getJabatan());
            pstmt.setBytes(6, anggota.getFotoProfil());
            pstmt.setInt(7, anggota.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Anggota anggota) throws SQLException {
        String sql = "DELETE FROM anggota WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, anggota.getId());
            pstmt.executeUpdate();
        }
    }
}