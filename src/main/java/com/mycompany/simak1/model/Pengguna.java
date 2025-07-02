/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.simak1.model;

/**
 *
 * @author Akmal Kurniawan
 */
public class Pengguna {
    private int id;
    private String username;
    private String passwordHash; // Menyimpan hash, bukan password asli
    private String levelAkses;

    // Getters and Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getLevelAkses() { return levelAkses; }
    public void setLevelAkses(String levelAkses) { this.levelAkses = levelAkses; }
}