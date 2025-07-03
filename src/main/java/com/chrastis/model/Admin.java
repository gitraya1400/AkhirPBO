package com.chrastis.model;

public abstract class Admin extends User {
    public Admin(String id, String nama, String role) {
        super(id, nama, role);
    }
}