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
    private final Activity activity;

<<<<<<< HEAD
    Application mApplication;

    public ActivityModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }
=======
    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides @PerActivity Activity activity() {
        return this.activity;
    }

>>>>>>> 3a2fd3db704c1e80b34c80c775e74ed3b8794240
}
