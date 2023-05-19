package com.example.logintest.data.model;

public class TextMessage {
    String userId;
    String text;
    String date;

    public TextMessage(String uid, String text, String date) {
        this.userId = uid;
        this.text = text;
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
