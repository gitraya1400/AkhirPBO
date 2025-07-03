package com.chrastis.model;

public class Mahasiswa extends User {
    private double ipk;
    private int tahunLulus; // <-- FIELD BARU
    private int totalKredit;

    public Mahasiswa(String id, String nama, double ipk, int tahunLulus, int totalKredit) {
        super(id, nama, "Mahasiswa");
        this.ipk = ipk;
        this.tahunLulus = tahunLulus; // <-- DITAMBAHKAN
        this.totalKredit = totalKredit;
    }

    // Getter & Setter untuk tahunLulus
    public int getTahunLulus() { return tahunLulus; }
    public void setTahunLulus(int tahunLulus) { this.tahunLulus = tahunLulus; }

    // ... getter/setter lain tidak berubah ...
    public double getIpk() { return ipk; }
    public void setIpk(double ipk) { this.ipk = ipk; }
    public int getTotalKredit() { return totalKredit; }
    public void setTotalKredit(int totalKredit) { this.totalKredit = totalKredit; }
    @Override
    public void showDashboard() { System.out.println("Menampilkan dashboard untuk Mahasiswa: " + getNama()); }
}