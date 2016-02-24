package com.example.achypur.notepadapp;

import java.util.Calendar;

public class User {
    private long id;
    private String login;

    public User() {}

    public  User(String login) {
        this.login = login;
    }

    public void setLogin(String login) {

        this.login = login;
    }

    public String getLogin() {

        return login;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
