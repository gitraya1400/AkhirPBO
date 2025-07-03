package com.chrastis.model;

public abstract class User {
    private String id;
    private String nama;
    private String role;

    public User(String id, String nama, String role) {
        this.id = id;
        this.nama = nama;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public abstract void showDashboard();
}
