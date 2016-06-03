package com.example.achypur.notepadapp.Component;

import android.app.Application;

import com.example.achypur.notepadapp.Application.NoteApplication;
import com.example.achypur.notepadapp.Managers.AccountManager;
import com.example.achypur.notepadapp.Managers.NoteManager;
import com.example.achypur.notepadapp.Module.ManagerModule;

import javax.inject.Singleton;
import dagger.*;

@Singleton
@Component(modules = ManagerModule.class)
public interface AppComponent {

    void inject(NoteApplication application);

    Application application();

    NoteManager noteManager();
    AccountManager accountManager();
}
