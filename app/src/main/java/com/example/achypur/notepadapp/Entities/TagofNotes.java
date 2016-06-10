package com.example.achypur.notepadapp.entities;

public class TagofNotes {
    private Long mId;
    private Long mNotesId;
    private Long mTagId;
    private Long mUserId;

    public TagofNotes() {
    }

    public TagofNotes(Long mId, Long mNotesId, Long mTagId, Long mUserId) {
        this.mId = mId;
        this.mNotesId = mNotesId;
        this.mTagId = mTagId;
        this.mUserId = mUserId;
    }

    public TagofNotes(Long mNotesId, Long mTagId, Long mUserId) {
        this(null, mNotesId, mTagId, mUserId);
    }

    public Long getmId() {
        return mId;
    }

    public void setmId(Long mId) {
        this.mId = mId;
    }

    public Long getmNotesId() {
        return mNotesId;
    }

    public void setmNotesId(Long mNotesId) {
        this.mNotesId = mNotesId;
    }

    public Long getmTagId() {
        return mTagId;
    }

    public void setmTagId(Long mTagId) {
        this.mTagId = mTagId;
    }

    public Long getmUserId() {
        return mUserId;
    }

    public void setmUserId(Long mUserId) {
        this.mUserId = mUserId;
    }

}
