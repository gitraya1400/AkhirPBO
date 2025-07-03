package com.chrastis.model;

public class SubKategoriKredit {
    private final int id;
    private final int idKategori;
    private final String namaSubKategori;
    private final int poin;

    public SubKategoriKredit(int id, int idKategori, String namaSubKategori, int poin) {
        this.id = id;
        this.idKategori = idKategori;
        this.namaSubKategori = namaSubKategori;
        this.poin = poin;
    }

    public int getId() { return id; }
    public int getIdKategori() { return idKategori; }
    public String getNamaSubKategori() { return namaSubKategori; }
    public int getPoin() { return poin; }

    @Override
    public String toString() {
        return namaSubKategori; // Ini juga penting untuk ComboBox
    }
}