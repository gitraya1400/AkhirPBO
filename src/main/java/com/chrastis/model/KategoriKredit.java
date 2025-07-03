package com.chrastis.model;

public class KategoriKredit {
    private final int id;
    private final String namaKategori;

    public KategoriKredit(int id, String namaKategori) {
        this.id = id;
        this.namaKategori = namaKategori;
    }

    public int getId() {
        return id;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    @Override
    public String toString() {
        return namaKategori; // Ini penting agar ComboBox menampilkan nama
    }
}