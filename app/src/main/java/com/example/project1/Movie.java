package com.example.project1;

import java.util.HashMap;
import java.util.Map;

public class Movie {
    private String title;
    private String director;
    private String description;
    private int year;
    private int duration;
    private int watchedMinutes;
    private boolean completed;
    private float rating;
    private String documentId;

    public Movie() {}

    public Movie(String title, String director, String description, int year, int duration, int watchedMinutes) {
        this.title = title;
        this.director = director;
        this.description = description;
        this.year = year;
        this.duration = duration;
        this.watchedMinutes = watchedMinutes;
        this.completed = false;
        this.rating = 0;
    }

    public Map<String, Object> toMap(String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("director", director);
        map.put("description", description);
        map.put("year", year);
        map.put("duration", duration);
        map.put("watchedMinutes", watchedMinutes);
        map.put("completed", completed);
        map.put("Rating", rating);
        map.put("userId", userId);
        return map;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String getDescription() {
        return description;
    }

    public int getYear() {
        return year;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getWatchedMinutes() {
        return watchedMinutes;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
