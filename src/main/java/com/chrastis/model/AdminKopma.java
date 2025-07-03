package com.chrastis.model;

public class AdminKopma extends Admin {
    public AdminKopma(String id, String nama) {
        super(id, nama, "AdminKopma");
    }

    @Override
    public void showDashboard() {
        System.out.println("Menampilkan dashboard untuk Admin KOPMA: " + getNama());
    }
}