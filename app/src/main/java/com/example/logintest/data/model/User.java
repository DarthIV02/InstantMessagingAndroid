package com.example.logintest.data.model;

public class User {
    private String userId;
    private String displayName;

    private int userIcon;

    public User(String userId, String displayName, int userIcon) {
        this.userId = userId;
        this.displayName = displayName;
        this.userIcon = userIcon;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getUserIcon(){ return userIcon; }
}
