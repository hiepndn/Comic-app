package com.example.comicapp;

public class Story {
    private String name;
    private int imageResId;

    public Story(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
