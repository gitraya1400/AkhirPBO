package com.chrastis.model;

import java.time.LocalDateTime;

public class Event {
    private String id;
    private String title;
    private String description;
    private String imagePath;
    private LocalDateTime eventDate;
    private LocalDateTime deadline;
    private String location;
    private String status;

    // Konstruktor, Getter, dan Setter
    public Event(String id, String title, String description, String imagePath, LocalDateTime eventDate, LocalDateTime deadline, String location, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.eventDate = eventDate;
        this.deadline = deadline;
        this.location = location;
        this.status = status;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }
    public LocalDateTime getEventDate() { return eventDate; }
    public LocalDateTime getDeadline() { return deadline; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
}