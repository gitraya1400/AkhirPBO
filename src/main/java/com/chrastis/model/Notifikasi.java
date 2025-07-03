package com.chrastis.model;

import java.time.LocalDateTime;

public class Notifikasi {
    private final String id;
    private final String mahasiswaId;
    private final String pesan;
    private final LocalDateTime tanggal;
    private String status;

    public Notifikasi(String id, String mahasiswaId, String pesan, LocalDateTime tanggal, String status) {
        this.id = id;
        this.mahasiswaId = mahasiswaId;
        this.pesan = pesan;
        this.tanggal = tanggal;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getMahasiswaId() {
        return mahasiswaId;
    }

    public String getPesan() {
        return pesan;
    }

    public LocalDateTime getTanggal() {
        return tanggal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}