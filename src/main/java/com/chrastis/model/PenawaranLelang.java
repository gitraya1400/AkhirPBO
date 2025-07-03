package com.chrastis.model;

import java.time.LocalDateTime;

public class PenawaranLelang {
    private String id;
    private Lelang lelang;
    private Mahasiswa mahasiswa;
    private int jumlahKreditDitawar;
    private LocalDateTime tanggalPenawaran;
    private String status;

    public PenawaranLelang(String id, Lelang lelang, Mahasiswa mahasiswa, int jumlahKreditDitawar, LocalDateTime tanggalPenawaran, String status) {
        this.id = id;
        this.lelang = lelang;
        this.mahasiswa = mahasiswa;
        this.jumlahKreditDitawar = jumlahKreditDitawar;
        this.tanggalPenawaran = tanggalPenawaran;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Lelang getLelang() { return lelang; }
    public void setLelang(Lelang lelang) { this.lelang = lelang; }
    public Mahasiswa getMahasiswa() { return mahasiswa; }
    public void setMahasiswa(Mahasiswa mahasiswa) { this.mahasiswa = mahasiswa; }
    public int getJumlahKreditDitawar() { return jumlahKreditDitawar; }
    public void setJumlahKreditDitawar(int jumlahKreditDitawar) { this.jumlahKreditDitawar = jumlahKreditDitawar; }
    public LocalDateTime getTanggalPenawaran() { return tanggalPenawaran; }
    public void setTanggalPenawaran(LocalDateTime tanggalPenawaran) { this.tanggalPenawaran = tanggalPenawaran; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}