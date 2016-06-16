package com.example.achypur.notepadapp.entities;

public class Picture {
    Long mId;
    Integer mHash;
    Long mNoteId;

    public Picture(Long Id, Integer hash, Long tagId) {
        mHash = hash;
        mId = Id;
        mNoteId = tagId;
    }


    public Long getmId() {
        return mId;
    }

    public void setmId(Long mId) {
        this.mId = mId;
    }

    public Picture(Integer hash, Long noteId) {
        this(null, hash, noteId);
    }

    public Integer getHash() {
        return mHash;
    }

    public void setHash(Integer hash) {
        this.mHash = hash;
    }

    public Long getNoteId() {
        return mNoteId;
    }

    public void setNoteId(Long mNoteId) {
        this.mNoteId = mNoteId;
    }
}
