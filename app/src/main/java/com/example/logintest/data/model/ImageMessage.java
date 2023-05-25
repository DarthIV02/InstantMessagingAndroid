package com.example.logintest.data.model;

import android.media.Image;

public class ImageMessage extends Message {
    String imageUID;

    public ImageMessage(String userName, String userId, String imageUID, String date) {
        super(userName, userId, date);
        this.imageUID = imageUID;
    }

    public String getImageUID() {
        return imageUID;
    }

    public void setImageUID(String imageUID) {
        this.imageUID = imageUID;
    }
}
