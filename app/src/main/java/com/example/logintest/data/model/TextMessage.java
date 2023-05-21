package com.example.logintest.data.model;

public class TextMessage extends Message{
    String text;

    public TextMessage(String userName, String userId, String text, String date) {
        super(userName, userId, date);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
