package com.example.logintest.data.model;

public class TextMessage {
    String userName;
    String text;
    String date;

    public TextMessage(String user_name, String text, String date) {
        this.userName = user_name;
        this.text = text;
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
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
}
