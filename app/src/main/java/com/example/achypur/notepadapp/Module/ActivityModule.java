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

    private final Context mContext;

    public ActivityModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    Context context() {
        return mContext;
    }

    @Provides
    @Singleton
    AccountManager provideAccountManager() {
        return new AccountManager(mContext);
    }


    @Provides
    @Singleton
    NoteManager provideNoteManager() {
        return new NoteManager(mContext);
    }
}
