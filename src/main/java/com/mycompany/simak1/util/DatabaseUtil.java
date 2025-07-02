/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.simak1.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    // --- SESUAIKAN DETAIL INI DENGAN DATABASE ANDA ---
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "simak_db"; // Ganti dengan nama database Anda di phpMyAdmin
    private static final String DB_USER = "root";     // Ganti dengan username database Anda
    private static final String DB_PASS = "";         // Ganti dengan password database Anda
    
    // URL koneksi untuk MySQL
    private static final String URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Koneksi ke MySQL
                connection = DriverManager.getConnection(URL, DB_USER, DB_PASS);
                System.out.println("Koneksi ke MySQL berhasil dibuat.");
                
                // Menonaktifkan pembuatan tabel otomatis dari Java untuk MySQL.
                // Disarankan membuat tabel langsung di phpMyAdmin.
                // initializeDatabase(connection); 
                
            } catch (SQLException e) {
                System.err.println("Gagal membuat koneksi ke MySQL: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }

    // Metode ini TIDAK DISARANKAN untuk dijalankan otomatis pada MySQL,
    // tapi sintaksnya sudah disesuaikan jika diperlukan.
    private static void initializeDatabase(Connection conn) {
        // Sintaks SQL disesuaikan untuk MySQL
        String createAnggotaTable = "CREATE TABLE IF NOT EXISTS anggota ("
            + "id INT PRIMARY KEY AUTO_INCREMENT,"
            + "nama VARCHAR(255) NOT NULL,"
            + "no_induk VARCHAR(255) UNIQUE NOT NULL,"
            + "jurusan VARCHAR(255),"
            + "tahun_masuk INT,"
            + "jabatan VARCHAR(255),"
            + "foto_profil BLOB"
            + ");";

        String createAcaraTable = "CREATE TABLE IF NOT EXISTS acara ("
            + "id INT PRIMARY KEY AUTO_INCREMENT,"
            + "nama_acara VARCHAR(255) NOT NULL,"
            + "tanggal DATE,"
            + "lokasi VARCHAR(255),"
            + "deskripsi TEXT"
            + ");";
        
        String createPenggunaTable = "CREATE TABLE IF NOT EXISTS pengguna ("
            + "id_pengguna INT PRIMARY KEY AUTO_INCREMENT,"
            + "username VARCHAR(255) UNIQUE NOT NULL,"
            + "password_hash VARCHAR(255) NOT NULL,"
            + "level_akses VARCHAR(50)"
            + ");";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createAnggotaTable);
            stmt.execute(createAcaraTable);
            stmt.execute(createPenggunaTable);
            System.out.println("Skema database berhasil diinisialisasi.");
        } catch (SQLException e) {
            System.err.println("Gagal menginisialisasi skema database: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
