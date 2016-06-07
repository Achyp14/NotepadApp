package com.example.achypur.notepadapp.Application;

import android.app.Application;

import com.example.achypur.notepadapp.Component.AppComponent;
import com.example.achypur.notepadapp.Component.DaggerAppComponent;
import com.example.achypur.notepadapp.Module.ManagerModule;



public class NoteApplication extends Application {

    private AppComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerAppComponent.builder().managerModule(new ManagerModule(this)).build();
    }

    public AppComponent component() {
        return  applicationComponent;
    }

}
