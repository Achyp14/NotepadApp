package com.example.achypur.notepadapp.jsonobjects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Forecast {

    @SerializedName("weather")
    private List<Weather> mWeather;

    @SerializedName("main")
    private Main mMain;

    @SerializedName("sys")
    private OtherInform mOtherInform;

    @SerializedName("wind")
    private Wind mWind;

    @SerializedName("rain")
    private Rain mRain;

    @SerializedName("name")
    private String mCity;

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    private byte[] icon;

    public Main getmMain() {
        return mMain;
    }

    public void setmMain(Main mMain) {
        this.mMain = mMain;
    }

    public OtherInform getmOtherInform() {
        return mOtherInform;
    }

    public void setmOtherInform(OtherInform mOtherInform) {
        this.mOtherInform = mOtherInform;
    }

    public Rain getmRain() {
        return mRain;
    }

    public void setmRain(Rain mRain) {
        this.mRain = mRain;
    }

    public List<Weather> getmWeather() {
        return mWeather;
    }

    public void setmWeather(List<Weather> mWeather) {
        this.mWeather = mWeather;
    }

    public Wind getmWind() {
        return mWind;
    }

    public void setmWind(Wind mWind) {
        this.mWind = mWind;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }
}
