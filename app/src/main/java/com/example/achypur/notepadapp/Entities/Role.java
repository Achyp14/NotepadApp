package com.example.achypur.notepadapp.Entities;

public class Role {
    private  Long mId;
    private  String mRole;

    public Role(Long mId, String mRole) {
        this.mId = mId;
        this.mRole = mRole;
    }

    public Long getmId() {
        return mId;
    }

    public void setmId(Long mId) {
        this.mId = mId;
    }

    public String getmRole() {
        return mRole;
    }

    public void setmRole(String mRole) {
        this.mRole = mRole;
    }
}
