package com.example.achypur.notepadapp.Entities;

public class Note{
    private Long mId;
    private String mTitle;
    private String mContent;
    private Long mUserId;
    private String mCreatedDate;
    private String mModifiedDate;
    private boolean mPolicyStatus;

    public Note() {
    }

    public Note(Long id, String mTitle, String mContent, Long mUserId, String mCreatedDate, String mModifiedDate, boolean mPolicyStatus) {
        this.mId = id;
        this.mTitle = mTitle;
        this.mContent = mContent;
        this.mUserId = mUserId;
        this.mCreatedDate = mCreatedDate;
        this.mModifiedDate = mModifiedDate;
        this.mPolicyStatus = mPolicyStatus;
    }

    public Note(String mTitle, String mContent, Long mUserId, String mCreatedDate, String mModifiedDate, boolean mPolicyStatus) {
       this(null, mTitle, mContent, mUserId, mCreatedDate, mModifiedDate, mPolicyStatus);
    }

    public Long getmId() {
        return mId;
    }

    public void setmId(Long mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getmCreatedDate() {
        return mCreatedDate;
    }

    public void setmCreatedDate(String mCreatedDate) {
        this.mCreatedDate = mCreatedDate;
    }

    public String getmModifiedDate() {
        return mModifiedDate;
    }

    public void setmModifiedDate(String mModifiedDate) {
        this.mModifiedDate = mModifiedDate;
    }

    public boolean getmPolicyStatus() {
        return mPolicyStatus;
    }

    public void setmPolicyId(boolean mPolicyStatus) {
        this.mPolicyStatus = mPolicyStatus;
    }

    public Long getmUserId() {
        return mUserId;
    }

    public void setmUserId(Long mUserId) {
        this.mUserId = mUserId;
    }
}
