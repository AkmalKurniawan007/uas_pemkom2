/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.simak1.dao;

/**
 *
 * @author Akmal Kurniawan
 */
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * KRITERIA UAS 2: Generic
 * Interface DAO generik. Tipe <T> memungkinkan antarmuka ini digunakan kembali
 * untuk model apa pun (Anggota, Acara, dll.). Ini adalah contoh bagus dari
 * "reusable code" dan desain berorientasi objek.
 */
public interface Dao<T> {
    Optional<T> get(int id) throws SQLException;
    List<T> getAll() throws SQLException;
    void save(T t) throws SQLException;
    void update(T t) throws SQLException;
    void delete(T t) throws SQLException;
}