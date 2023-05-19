package com.example.logintest.data.model;

public class TextMessage {
    String userName;

    String userId;
    String text;
    String date;

    public TextMessage(String userName, String userId, String text, String date) {
        this.userName = userName;
        this.userId = userId;
        this.text = text;
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userId) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
