package com.example.achypur.notepadapp.Application;


import android.app.Application;
import android.util.Log;

import com.example.achypur.notepadapp.Component.AppComponent;
import com.example.achypur.notepadapp.Component.DaggerAppComponent;
import com.example.achypur.notepadapp.Managers.AccountManager;
import com.example.achypur.notepadapp.Managers.NoteManager;
import com.example.achypur.notepadapp.Module.ActivityModule;
import com.example.achypur.notepadapp.Module.ManagerModule;

import javax.inject.Inject;


public class NoteApplication extends Application {

    private AppComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerAppComponent.builder().managerModule(new ManagerModule()).
                activityModule(new ActivityModule(this)).build();

    }

    public AppComponent component() {
        return  applicationComponent;
    }

}
