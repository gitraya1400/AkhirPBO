package com.chrastis.model;

import java.sql.Date;

public class TransaksiKopma {
    private int id;
    private String mahasiswaId;
    private String barangId; // Diubah dari int ke String
    private String kodePenukaran;
    private Date tanggal;
    private String status;

    public TransaksiKopma(int id, String mahasiswaId, String barangId, String kodePenukaran, Date tanggal, String status) {
        this.id = id;
        this.mahasiswaId = mahasiswaId;
        this.barangId = barangId;
        this.kodePenukaran = kodePenukaran;
        this.tanggal = tanggal;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getMahasiswaId() {
        return mahasiswaId;
    }

    public String getBarangId() {
        return barangId;
    }

    public String getKodePenukaran() {
        return kodePenukaran;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}