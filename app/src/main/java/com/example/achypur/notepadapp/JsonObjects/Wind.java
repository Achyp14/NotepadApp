package com.example.achypur.notepadapp.jsonobjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by achypur on 18.05.2016.
 */
public class Wind {

    @SerializedName("speed")
    private double mSpeed;

    public double getSpeed() {
        return mSpeed;
    }

    public void setSpeed(double speed) {
        mSpeed = speed;
    }
}
