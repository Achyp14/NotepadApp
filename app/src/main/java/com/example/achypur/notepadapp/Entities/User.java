package com.example.achypur.notepadapp.entities;


public class User {
    private Long mId;
    private String mLogin;
    private String mName;
    private String mEmail;
    private String mPassword;
    private String mRole;
    private byte[] mImage;

    public User() {
    }

    public User(Long id, String login, String name, String email, String password, String role, byte[] image) {
        mId = id;
        mLogin = login;
        mName = name;
        mEmail = email;
        mPassword = password;
        mRole =  role;
        mImage = image;
    }

    public User(String login, String name, String email, String password, String role, byte[] image) {
       this(null, login, name, email, password, role, image);
    }

    public void setLogin(String login) {

        mLogin = login;
    }

    public String getLogin() {

        return mLogin;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getRole() {
        return mRole;
    }

    public void setRole(String role) {
        mRole = role;
    }

    public  byte[] getImage() {
        return mImage;
    }

    public void setImage(byte[] image) {
        mImage = image;
    }
}
