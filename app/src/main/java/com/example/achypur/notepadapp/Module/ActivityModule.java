package com.example.achypur.notepadapp.Module;

import android.app.Activity;
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

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides @PerActivity Activity activity() {
        return this.activity;
    }

}
