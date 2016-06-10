package com.example.achypur.notepadapp.entities;

public class Coordinate {
    private Long mId;
    private double mLatitude;
    private double mLongtitude;

    public Coordinate(Long mId, double mLatitude, double mLongtitude) {
        this.mId = mId;
        this.mLatitude = mLatitude;
        this.mLongtitude = mLongtitude;
    }

    public Coordinate(double mLatitude, double mLongtitude) {
        this(null, mLatitude, mLongtitude);
        this.mLatitude = mLatitude;
        this.mLongtitude = mLongtitude;
    }


    public Long getId() {
        return mId;
    }

    public void setId(Long mId) {
        this.mId = mId;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongtitude() {
        return mLongtitude;
    }

    public void setLongtitude(double mLongtitude) {
        this.mLongtitude = mLongtitude;
    }

}
