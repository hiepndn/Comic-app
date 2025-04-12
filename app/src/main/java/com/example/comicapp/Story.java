package com.example.comicapp;

import java.util.Map;

public class Story {
    private String id;
    private String name;
    private String category;
    private String des;
    private String img;
    private String author;
    private Map<String, Object> chapter; // Nếu bạn có danh sách chapter

    public Story() {
        // Constructor rỗng cần thiết cho Firebase
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDes() {
        return des;
    }

    public String getImg() {
        return img;
    }

    public String getAuthor() {
        return author;
    }

    public Map<String, Object> getChapter() {
        return chapter;
    }

    // Nếu cần set dữ liệu:
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setChapter(Map<String, Object> chapter) {
        this.chapter = chapter;
    }
}
