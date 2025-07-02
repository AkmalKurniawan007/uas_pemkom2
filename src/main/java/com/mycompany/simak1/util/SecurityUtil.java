/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.simak1.util;

/**
 *
 * @author Akmal Kurniawan
 */
import org.mindrot.jbcrypt.BCrypt;

/**
 * Kelas utilitas untuk menangani keamanan.
 * KRITERIA UAS 6: Cryptography
 * - Menggunakan bcrypt untuk hashing password, yang merupakan standar industri.
 * - Salt dihasilkan secara otomatis oleh library BCrypt untuk setiap password.
 */
public class SecurityUtil {

    /**
     * Menghasilkan hash dari password menggunakan BCrypt.
     * @param plainPassword Password teks biasa.
     * @return String hash dari password.
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Memeriksa apakah password teks biasa cocok dengan hash yang tersimpan.
     * @param plainPassword Password yang diinput pengguna.
     * @param hashedPassword Hash yang disimpan di database.
     * @return true jika cocok, false jika tidak.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
