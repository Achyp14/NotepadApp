package com.example.achypur.notepadapp.Entities;

public class Tag {
    private Long mId;
    private String mTag;

    public Tag() {
    }

    public Tag(Long mId, String mTag) {
        this.mId = mId;
        this.mTag = mTag;
    }

    public Tag(String mTag) {
        this(null, mTag);
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

    @Override
    public boolean equals(Object o) {
        Tag tag = (Tag) o;
        if (this.getmTag().equals(tag.getmTag())) {
            return true;
        } else {
            return false;
        }
    }
}
