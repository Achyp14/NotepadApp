package com.example.achypur.notepadapp.Module;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.example.achypur.notepadapp.Managers.AccountManager;
import com.example.achypur.notepadapp.Managers.NoteManager;
import com.example.achypur.notepadapp.PerActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    Application mApplication;

    public ActivityModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }
}
