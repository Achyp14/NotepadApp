package com.example.achypur.notepadapp.Module;

import android.app.Application;
import android.content.Context;

import com.example.achypur.notepadapp.Application.NoteApplication;
import com.example.achypur.notepadapp.Managers.AccountManager;
import com.example.achypur.notepadapp.Managers.NoteManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ManagerModule {

    private Application mApplication;

    public ManagerModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    AccountManager provideAccountManager() {
        return new AccountManager(mApplication);
    }


    @Provides
    @Singleton
    NoteManager provideNoteManager() {
        return new NoteManager(mApplication);
    }
}
