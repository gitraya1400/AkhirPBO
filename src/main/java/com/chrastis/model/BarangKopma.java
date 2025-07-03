package com.chrastis.model;

public class BarangKopma {
    private String id;
    private String nama;
    private int hargaKredit;
    private int stok;
    private String deskripsi;
    private String kategori;
    private String gambar;

    public BarangKopma() {}

    public BarangKopma(String id, String nama, int hargaKredit, int stok, String deskripsi, String kategori) {
        this.id = id;
        this.nama = nama;
        this.hargaKredit = hargaKredit;
        this.stok = stok;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public int getHargaKredit() { return hargaKredit; }
    public void setHargaKredit(int hargaKredit) { this.hargaKredit = hargaKredit; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getGambar() { return gambar; }
    public void setGambar(String gambar) { this.gambar = gambar; }

    @Override
    public String toString() {
        return nama + " (" + hargaKredit + " kredit)";
    }
}
