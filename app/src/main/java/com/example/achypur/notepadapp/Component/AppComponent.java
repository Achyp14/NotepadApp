package com.example.achypur.notepadapp.Component;

import android.app.Activity;

import com.example.achypur.notepadapp.Module.ActivityModule;
import com.example.achypur.notepadapp.Module.ManagerModule;

import javax.inject.Singleton;
import dagger.*;

@Singleton
@Component(modules = {ManagerModule.class, ActivityModule.class})
public interface AppComponent {

    void inject(Activity activity);

}
