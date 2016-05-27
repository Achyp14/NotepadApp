package com.example.achypur.notepadapp.Application;


import android.app.Application;

import com.example.achypur.notepadapp.Managers.AccountManager;
import com.example.achypur.notepadapp.Managers.NoteManager;
import com.example.achypur.notepadapp.Managers.TagManager;

public class NoteApplication extends Application {
    private static AccountManager sAccountManager;
    private static NoteManager sNoteManager;
    private static TagManager sTagManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sAccountManager = new AccountManager(this);
        sNoteManager = new NoteManager(this);
    }

    public static AccountManager getsAccountManager() {
        return sAccountManager;
    }
    public static NoteManager getsNoteManager() {
        return sNoteManager;
    }

    public static TagManager getsTagManager() {
        return sTagManager;
    }
}
