package com.chrastis.model;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderboardManager {
    public static List<Mahasiswa> getTopTenMahasiswa(List<User> users) {
        return users.stream()
                .filter(u -> u instanceof Mahasiswa)
                .map(u -> (Mahasiswa) u)
                .sorted(Comparator.comparingInt(Mahasiswa::getTotalKredit).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}