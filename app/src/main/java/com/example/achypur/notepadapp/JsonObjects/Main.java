package com.example.achypur.notepadapp.JsonObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by achypur on 18.05.2016.
 */
public class Main {

    @SerializedName("temp")
    private double mTemperature;

    public double getmTemperature() {
        return mTemperature;
    }

    public void setmTemperature(double mTemperature) {
        this.mTemperature = mTemperature;
    }
}
