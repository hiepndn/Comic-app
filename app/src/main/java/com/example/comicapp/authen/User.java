package com.example.comicapp.authen;

public class User {
    private String userName;
    private String psw;
    private int role;

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