package com.example.achypur.notepadapp.Entities;

/**
 * Created by achypur on 07.04.2016.
 */
public class Picture {
    Long mId;
    byte[] mByteArray;
    Long mTagId;

    public Picture(Long Id, byte[] byteArray, Long tagId) {
        mByteArray = byteArray;
        mId = Id;
        mTagId = tagId;
    }


    public Picture(byte[] byteArray, Long tagId) {
        this(null, byteArray, tagId);
    }

    public byte[] getByteArray() {
        return mByteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.mByteArray = byteArray;
    }

    public Long getmId() {
        return mId;
    }

    public void setmId(Long mId) {
        this.mId = mId;
    }

    public Long getmTagId() {
        return mTagId;
    }

    public void setmTagId(Long mTagId) {
        this.mTagId = mTagId;
    }
}
