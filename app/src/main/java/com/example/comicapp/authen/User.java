package com.example.comicapp.authen;

public class User {
    private String userName;
    private String psw;
    private int role;
    private String userKey;

    public User() {};

    public User(String userName , String psw, int role) {
        this.userName = userName;
        this.psw = psw;
        this.role = role;
    }

    public String getPsw() {
        return psw;
    }

    public String getUserName() {
        return userName;
    }

    public int getRole() {
        return role;
    }
    public String getUserKey() {
        return userKey;
    }
    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
    public void setRole(int role) {
        this.role = role;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}