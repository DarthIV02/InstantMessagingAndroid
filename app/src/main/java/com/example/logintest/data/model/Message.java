package com.example.logintest.data.model;

public class Message {

    String userName;

    String userId;

    String date;

    public Message(String userName, String userId, String date) {
        this.userName = userName;
        this.userId = userId;
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
