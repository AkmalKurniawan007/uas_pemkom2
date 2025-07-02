/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.simak1.dao;

/**
 *
 * @author Akmal Kurniawan
 */
import com.mycompany.simak1.model.Pengguna;
import com.mycompany.simak1.util.DatabaseUtil;
import com.mycompany.simak1.util.SecurityUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PenggunaDao {
    private Connection conn = DatabaseUtil.getConnection();
    
    /**
     * Menyimpan pengguna baru dengan password yang sudah di-hash.
     * Ini menunjukkan gabungan fitur DAO dan Cryptography.
     */
    public void save(Pengguna pengguna, String plainPassword) throws SQLException {
        String sql = "INSERT INTO pengguna(username, password_hash, level_akses) VALUES(?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pengguna.getUsername());
            // KRITERIA UAS 6: Hash password sebelum disimpan
            String hashedPassword = SecurityUtil.hashPassword(plainPassword);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, pengguna.getLevelAkses());
            pstmt.executeUpdate();
        }
    }
}