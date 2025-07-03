package com.chrastis.model;

public class AdminKredit extends Admin {
    public AdminKredit(String id, String nama) {
        super(id, nama, "AdminKredit");
    }

    @Override
    public void showDashboard() {
        System.out.println("Menampilkan dashboard untuk Admin Kredit: " + getNama());
    }
}