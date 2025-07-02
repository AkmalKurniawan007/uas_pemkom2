/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.simak1.dao;

/**
 *
 * @author Akmal Kurniawan
 */

import com.mycompany.simak1.model.Kas;
import com.mycompany.simak1.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KasDao implements Dao<Kas> {

    private Connection conn = DatabaseUtil.getConnection();

    @Override
    public List<Kas> getAll() throws SQLException {
        List<Kas> kasList = new ArrayList<>();
        // Query dengan JOIN ke tabel anggota dan acara
        String sql = "SELECT k.id, k.id_anggota, a.nama as nama_anggota, k.id_acara, ac.nama_acara, k.tanggal, k.jenis, k.jumlah, k.deskripsi " +
                     "FROM kas k " +
                     "LEFT JOIN anggota a ON k.id_anggota = a.id " +
                     "LEFT JOIN acara ac ON k.id_acara = ac.id " +
                     "ORDER BY k.tanggal DESC, k.id DESC";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                kasList.add(mapResultSetToKas(rs));
            }
        }
        return kasList;
    }

    @Override
    public void save(Kas kas) throws SQLException {
        String sql = "INSERT INTO kas (id_anggota, id_acara, tanggal, jenis, jumlah, deskripsi) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (kas.getIdAnggota() != null) {
                pstmt.setInt(1, kas.getIdAnggota());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            if (kas.getIdAcara() != null) {
                pstmt.setInt(2, kas.getIdAcara());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            
            pstmt.setDate(3, Date.valueOf(kas.getTanggal()));
            pstmt.setString(4, kas.getJenis());
            pstmt.setDouble(5, kas.getJumlah());
            pstmt.setString(6, kas.getDeskripsi());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Kas kas) throws SQLException {
        String sql = "DELETE FROM kas WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, kas.getId());
            pstmt.executeUpdate();
        }
    }
    
    private Kas mapResultSetToKas(ResultSet rs) throws SQLException {
        Kas kas = new Kas();
        kas.setId(rs.getInt("id"));
        kas.setIdAnggota((Integer) rs.getObject("id_anggota"));
        kas.setNamaAnggota(rs.getString("nama_anggota"));
        kas.setIdAcara((Integer) rs.getObject("id_acara"));
        kas.setNamaAcara(rs.getString("nama_acara"));
        kas.setTanggal(rs.getDate("tanggal").toLocalDate());
        kas.setJenis(rs.getString("jenis"));
        kas.setJumlah(rs.getDouble("jumlah"));
        kas.setDeskripsi(rs.getString("deskripsi"));
        return kas;
    }

    // Metode lain (get, update) bisa diimplementasikan jika perlu
    @Override
    public Optional<Kas> get(int id) throws SQLException { return Optional.empty(); }
    @Override
    public void update(Kas kas) throws SQLException { /* ... implementasi jika perlu ... */ }
}

