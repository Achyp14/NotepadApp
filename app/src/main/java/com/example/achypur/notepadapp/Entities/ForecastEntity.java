package com.example.achypur.notepadapp.entities;


public class ForecastEntity {

    Long mId;
    Long mNoteId;
    byte[] mIcon;
    String mDescription;
    String mCountry;
    String mCity;
    Double mTemp;
    Double mWind;
    Double mRain;

    public ForecastEntity(Long id, Long noteId, byte[] icon, String description, String country, String city, Double temp, Double rain, Double wind) {
        mId = id;
        mNoteId = noteId;
        mIcon = icon;
        mDescription = description;
        mCountry = country;
        mCity = city;
        mTemp = temp;
        mRain = rain;
        mWind = wind;

    }

    public ForecastEntity(Long noteId, byte[] icon, String description, String country, String city, Double temp, Double rain, Double wind) {
        this(null,noteId, icon, description, country, city, temp, rain, wind);
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmCountry() {
        return mCountry;
    }

    public void setmCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public byte[] getmIcon() {
        return mIcon;
    }

    public void setmIcon(byte[] mIcon) {
        this.mIcon = mIcon;
    }

    public Long getmId() {
        return mId;
    }

    public void setmId(Long mId) {
        this.mId = mId;
    }

    public Long getmNoteId() {
        return mNoteId;
    }

    public void setmNoteId(Long mNoteId) {
        this.mNoteId = mNoteId;
    }

    public Double getmRain() {
        return mRain;
    }

    public void setmRain(Double mRain) {
        this.mRain = mRain;
    }

    public Double getmTemp() {
        return mTemp;
    }

    public void setmTemp(Double mTemp) {
        this.mTemp = mTemp;
    }

    public Double getmWind() {
        return mWind;
    }

    public void setmWind(Double mWind) {
        this.mWind = mWind;
    }
}
