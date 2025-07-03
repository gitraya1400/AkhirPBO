package com.chrastis.model;

public class Kredit {
    private final String id;
    private final String jenis;
    private final String subJenis;
    private final int nilai;

    public Kredit(String id, String jenis, String subJenis, int nilai) {
        this.id = id;
        this.jenis = jenis;
        this.subJenis = subJenis;
        this.nilai = nilai;
    }

    public String getId() {
        return id;
    }

    public String getJenis() {
        return jenis;
    }

    public String getSubJenis() {
        return subJenis;
    }

    public int getNilai() {
        return nilai;
    }
}