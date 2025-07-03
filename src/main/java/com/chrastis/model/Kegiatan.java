package com.chrastis.model;

import java.time.LocalDate;

public class Kegiatan {
    private String id;
    private String nama;
    private String jenis;
    private int poin;
    private LocalDate tanggalAkhir;

    public Kegiatan(String id, String nama, String jenis, int poin, LocalDate tanggalAkhir) {
        this.id = id;
        this.nama = nama;
        this.jenis = jenis;
        this.poin = poin;
        this.tanggalAkhir = tanggalAkhir;
    }

    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getJenis() { return jenis; }
    public int getPoin() { return poin; }
    public LocalDate getTanggalAkhir() { return tanggalAkhir; }
}