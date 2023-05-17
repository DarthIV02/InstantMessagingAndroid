package com.example.logintest.data.model;

import android.graphics.Color;

import java.util.Random;

public class RandomColor {

    private Color new_Color;

    public RandomColor() {
        Random rand = new Random();

        float r = (float) (rand.nextFloat() / 2f);
        float g = (float) (rand.nextFloat() / 2f);
        float b = (float) (rand.nextFloat() / 2f);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            this.new_Color = Color.valueOf(r, g, b);
        }
    }

    public Color getColor(){
        return this.new_Color;
    }
}
