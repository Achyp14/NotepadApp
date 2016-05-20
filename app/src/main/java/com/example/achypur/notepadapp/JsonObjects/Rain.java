package com.example.achypur.notepadapp.JsonObjects;

import com.google.gson.annotations.SerializedName;


public class Rain {

    @SerializedName("3h")
    private double mCount;

    public double getmCount() {
        return mCount;
    }

    public void setmCount(double mCount) {
        this.mCount = mCount;
    }
}
