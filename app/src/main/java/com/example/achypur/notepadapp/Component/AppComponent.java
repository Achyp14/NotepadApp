package com.example.achypur.notepadapp.component;

import android.app.Activity;
import android.app.Application;

import com.example.achypur.notepadapp.managers.AccountManager;
import com.example.achypur.notepadapp.managers.NoteManager;
import com.example.achypur.notepadapp.module.ActivityModule;
import com.example.achypur.notepadapp.module.ManagerModule;

import javax.inject.Singleton;
import dagger.*;

@Singleton
@Component(modules = {ManagerModule.class, ActivityModule.class})
public interface AppComponent {

    void inject(Activity activity);

    Application application();

    NoteManager noteManager();
    AccountManager accountManager();
}
