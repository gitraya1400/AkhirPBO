package com.chrastis.model;

import java.time.LocalDate;

public class PengajuanKredit {
    private final String id;
    private final Mahasiswa mahasiswa;
    private final String kreditId; // Bisa jadi null jika tidak terkait kredit spesifik
    private final String bukti;
    private final LocalDate tanggal;
    private String status;
    private int nilai;
    private String keterangan;

    // Field baru
    private String kategori;
    private String subKategori;
    private String deskripsi;

    public PengajuanKredit(String id, Mahasiswa mahasiswa, String kreditId, String bukti, LocalDate tanggal) {
        this.id = id;
        this.mahasiswa = mahasiswa;
        this.kreditId = kreditId;
        this.bukti = bukti;
        this.tanggal = tanggal;
        this.status = "Menunggu";
    }

    // Getters dan Setters untuk semua field, termasuk yang baru...
    public String getId() { return id; }
    public Mahasiswa getMahasiswa() { return mahasiswa; }
    public String getKreditId() { return kreditId; }
    public String getBukti() { return bukti; }
    public LocalDate getTanggal() { return tanggal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getNilai() { return nilai; }
    public void setNilai(int nilai) { this.nilai = nilai; }
    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public String getSubKategori() { return subKategori; }
    public void setSubKategori(String subKategori) { this.subKategori = subKategori; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
}