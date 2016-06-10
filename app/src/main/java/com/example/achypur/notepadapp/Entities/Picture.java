package com.example.achypur.notepadapp.entities;

/**
 * Created by achypur on 07.04.2016.
 */
public class Picture {
    Long mId;
    byte[] mByteArray;
    Long mNoteId;

    public Picture(Long Id, byte[] byteArray, Long tagId) {
        mByteArray = byteArray;
        mId = Id;
        mNoteId = tagId;
    }


    public Picture(byte[] byteArray, Long noteId) {
        this(null, byteArray, noteId);
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

    public Long getmNoteId() {
        return mNoteId;
    }

    public void setmNoteId(Long mNoteId) {
        this.mNoteId = mNoteId;
    }
}
