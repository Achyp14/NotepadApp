package com.example.achypur.notepadapp;

import java.io.Serializable;

/**
 * Created by achypur on 17.02.2016.
 */
public class Note implements Serializable{
    private String title;
    private String text;

   public Note(String title, String text) {
       this.title = title;
       this.text = text;
   }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
