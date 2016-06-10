package com.example.achypur.notepadapp;

import android.app.Application;

import com.example.achypur.notepadapp.component.AppComponent;
import com.example.achypur.notepadapp.component.DaggerAppComponent;
import com.example.achypur.notepadapp.module.ManagerModule;

public class NoteApplication extends Application {

    private AppComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerAppComponent.builder().managerModule(new ManagerModule(this)).build();
    }

    public AppComponent component() {
        return applicationComponent;
    }
}

