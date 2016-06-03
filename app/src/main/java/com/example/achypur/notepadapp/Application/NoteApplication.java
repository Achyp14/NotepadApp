package com.example.achypur.notepadapp.Application;


import android.app.Application;
import android.util.Log;

import com.example.achypur.notepadapp.Component.AppComponent;
import com.example.achypur.notepadapp.Component.DaggerAppComponent;
import com.example.achypur.notepadapp.Managers.AccountManager;
import com.example.achypur.notepadapp.Managers.NoteManager;
import com.example.achypur.notepadapp.Module.ManagerModule;

import javax.inject.Inject;


public class NoteApplication extends Application {

    private AppComponent applicationComponent;

    @Inject
    AccountManager sAccountManager;

    @Inject
    NoteManager sNoteManager;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerAppComponent.builder().managerModule(new ManagerModule(this)).
                build();

        applicationComponent.inject(this);

        sAccountManager.createUserRepository();

    }

    public AppComponent component() {
        return  applicationComponent;
    }

    public AccountManager getACc() {
        return  sAccountManager;
    }

}
