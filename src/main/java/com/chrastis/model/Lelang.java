package com.chrastis.model;

import java.time.LocalDateTime;

public class Lelang {
    private String id;
    private String lokasi;
    private int minimalKredit;
    private int kuota;
    private int tahunLelang; // <-- FIELD BARU
    private String deskripsi;
    private LocalDateTime tanggalSelesai;
    private String status;

    public Lelang(String id, String lokasi, int minimalKredit, int kuota, int tahunLelang, String deskripsi, LocalDateTime tanggalSelesai, String status) {
        this.id = id;
        this.lokasi = lokasi;
        this.minimalKredit = minimalKredit;
        this.kuota = kuota;
        this.tahunLelang = tahunLelang; // <-- DITAMBAHKAN
        this.deskripsi = deskripsi;
        this.tanggalSelesai = tanggalSelesai;
        this.status = status;
    }

    // Getter & Setter untuk tahunLelang
    public int getTahunLelang() { return tahunLelang; }
    public void setTahunLelang(int tahunLelang) { this.tahunLelang = tahunLelang; }

    // ... getter/setter lain tidak berubah ...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    public int getMinimalKredit() { return minimalKredit; }
    public void setMinimalKredit(int minimalKredit) { this.minimalKredit = minimalKredit; }
    public int getKuota() { return kuota; }
    public void setKuota(int kuota) { this.kuota = kuota; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public LocalDateTime getTanggalSelesai() { return tanggalSelesai; }
    public void setTanggalSelesai(LocalDateTime tanggalSelesai) { this.tanggalSelesai = tanggalSelesai; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}