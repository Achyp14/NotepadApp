package com.example.achypur.notepadapp.jsonobjects;

import com.google.gson.annotations.*;

public class Weather {

    @SerializedName("description")
    private String mDescription;

    @SerializedName("icon")
    private String mIconWeatherId;

    public String getmIconWeatherId() {
        return mIconWeatherId;
    }

    public void setmIconWeatherId(String mIconWeatherId) {
        this.mIconWeatherId = mIconWeatherId;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mMain) {
        this.mDescription = mMain;
    }
}
