package com.example.achypur.notepadapp.jsonobjects;


import com.google.gson.annotations.SerializedName;

public class OtherInform {

    @SerializedName("country")
    private String mCountry;

    public String getmCountry() {
        return mCountry;
    }

    public void setmCountry(String mCountry) {
        this.mCountry = mCountry;
    }
}
