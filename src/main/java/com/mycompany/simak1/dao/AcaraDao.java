package com.mycompany.simak1.dao;

import com.mycompany.simak1.model.Acara;
import com.mycompany.simak1.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Kelas DAO untuk mengelola data Acara di database.
 * Perbaikan: Menambahkan logika untuk menyimpan, memperbarui, dan membaca kolom budget.
 */
public class AcaraDao implements Dao<Acara> {

    private Connection conn = DatabaseUtil.getConnection();

    @Override
    public Optional<Acara> get(int id) throws SQLException {
        String sql = "SELECT * FROM acara WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToAcara(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Acara> getAll() throws SQLException {
        List<Acara> acaraList = new ArrayList<>();
        String sql = "SELECT * FROM acara";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                acaraList.add(mapResultSetToAcara(rs));
            }
        }
        return acaraList;
    }

    @Override
    public void save(Acara acara) throws SQLException {
        // SQL diperbarui dengan kolom budget
        String sql = "INSERT INTO acara (nama_acara, tanggal, lokasi, deskripsi, budget) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, acara.getNamaAcara());
            pstmt.setDate(2, Date.valueOf(acara.getTanggal()));
            pstmt.setString(3, acara.getLokasi());
            pstmt.setString(4, acara.getDeskripsi());
            pstmt.setDouble(5, acara.getBudget()); // Menyimpan nilai budget
            pstmt.executeUpdate();
        }
    }

    @Override
    public void update(Acara acara) throws SQLException {
        // SQL diperbarui dengan kolom budget
        String sql = "UPDATE acara SET nama_acara = ?, tanggal = ?, lokasi = ?, deskripsi = ?, budget = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, acara.getNamaAcara());
            pstmt.setDate(2, Date.valueOf(acara.getTanggal()));
            pstmt.setString(3, acara.getLokasi());
            pstmt.setString(4, acara.getDeskripsi());
            pstmt.setDouble(5, acara.getBudget()); // Memperbarui nilai budget
            pstmt.setInt(6, acara.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Acara acara) throws SQLException {
        String sql = "DELETE FROM acara WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, acara.getId());
            pstmt.executeUpdate();
        }
    }

    private Acara mapResultSetToAcara(ResultSet rs) throws SQLException {
        Acara acara = new Acara();
        acara.setId(rs.getInt("id"));
        acara.setNamaAcara(rs.getString("nama_acara"));
        acara.setTanggal(rs.getDate("tanggal").toLocalDate());
        acara.setLokasi(rs.getString("lokasi"));
        acara.setDeskripsi(rs.getString("deskripsi"));
        acara.setBudget(rs.getDouble("budget")); // Membaca nilai budget
        return acara;
    }
}
    