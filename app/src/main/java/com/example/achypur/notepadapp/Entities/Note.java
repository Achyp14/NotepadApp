package com.example.achypur.notepadapp.Entities;

public class Note {
    private Long mId;
    private String mTitle;
    private String mContent;
    private Long mUserId;
    private String mCreatedDate;
    private String mModifiedDate;
    private Boolean mPolicyStatus;
    private Long mLocation;

    public Note() {
    }

    public Note(Long id, String mTitle, String mContent, Long mUserId, String mCreatedDate, String mModifiedDate, Boolean mPolicyStatus, Long mLocation) {
        this.mId = id;
        this.mTitle = mTitle;
        this.mContent = mContent;
        this.mUserId = mUserId;
        this.mCreatedDate = mCreatedDate;
        this.mModifiedDate = mModifiedDate;
        this.mPolicyStatus = mPolicyStatus;
        this.mLocation = mLocation;
    }

    public Note(String mTitle, String mContent, Long mUserId, String mCreatedDate, String mModifiedDate, Boolean mPolicyStatus, Long mLocation) {
        this(null, mTitle, mContent, mUserId, mCreatedDate, mModifiedDate, mPolicyStatus, mLocation);
    }

    public Long getmId() {
        return mId;
    }

    public Long getmLocation() {
        return mLocation;
    }

    public void setmLocation(Long mLocation) {
        this.mLocation = mLocation;
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

    public Boolean getmPolicyStatus() {
        return mPolicyStatus;
    }

    public void setmPolicyId(Boolean mPolicyStatus) {
        this.mPolicyStatus = mPolicyStatus;
    }

    public Long getmUserId() {
        return mUserId;
    }

    public void setmUserId(Long mUserId) {
        this.mUserId = mUserId;
    }


}
