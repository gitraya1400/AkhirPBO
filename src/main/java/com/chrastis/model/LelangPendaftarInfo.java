package com.chrastis.model;

public class LelangPendaftarInfo {
    private final String namaMahasiswa;
    private final String nim;
    private final int tahunLulus;
    private final String lokasiLelang;
    private final int jumlahTawaran;
    private final int kuota;

    public LelangPendaftarInfo(String namaMahasiswa, String nim, int tahunLulus, String lokasiLelang, int jumlahTawaran, int kuota) {
        this.namaMahasiswa = namaMahasiswa;
        this.nim = nim;
        this.tahunLulus = tahunLulus;
        this.lokasiLelang = lokasiLelang;
        this.jumlahTawaran = jumlahTawaran;
        this.kuota = kuota;
    }

    // Getters
    public String getNamaMahasiswa() { return namaMahasiswa; }
    public String getNim() { return nim; }
    public int getTahunLulus() { return tahunLulus; }
    public String getLokasiLelang() { return lokasiLelang; }
    public int getJumlahTawaran() { return jumlahTawaran; }
    public int getKuota() { return kuota; }
}