package com.example.achypur.notepadapp.module;

import android.app.Application;

import com.example.achypur.notepadapp.managers.AccountManager;
import com.example.achypur.notepadapp.managers.NoteManager;

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
