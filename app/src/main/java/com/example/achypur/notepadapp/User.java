package com.example.achypur.notepadapp;

import java.util.Calendar;

public class User {
    private int id;
    private String login;
    private boolean logged;

    public User() {}

    public  User(String login, boolean logged) {
        this.login = login;
        this.logged = logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public void setLogin(String login) {

        this.login = login;
    }

    public String getLogin() {

        return login;
    }

    public boolean getLogged() {
        return logged;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
