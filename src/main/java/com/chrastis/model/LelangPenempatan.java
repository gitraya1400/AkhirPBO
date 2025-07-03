package com.chrastis.model;

public class LelangPenempatan {
    private String id;
    private String lokasi;
    private int minimalKredit;
    private Mahasiswa pemenang;

    public LelangPenempatan(String id, String lokasi, int minimalKredit) {
        this.id = id;
        this.lokasi = lokasi;
        this.minimalKredit = minimalKredit;
        this.pemenang = null;
    }

    public String getId() {
        return id;
    }

    public String getLokasi() {
        return lokasi;
    }

    public int getMinimalKredit() {
        return minimalKredit;
    }

    public Mahasiswa getPemenang() {
        return pemenang;
    }

    public void setPemenang(Mahasiswa pemenang) {
        this.pemenang = pemenang;
    }
}
