package com.example.achypur.notepadapp.Entities;

public class Tag {
    private  Long mId;
    private  String mTag;

    public Tag(){}

    public Tag(Long mId, String mTag) {
        this.mId = mId;
        this.mTag = mTag;
    }

    public Long getmId() {
        return mId;
    }

    public void setmId(Long mId) {
        this.mId = mId;
    }

    public String getmTag() {
        return mTag;
    }

    public void setmTag(String mTag) {
        this.mTag = mTag;
    }
}
