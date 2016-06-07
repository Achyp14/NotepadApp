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

    @Provides
    @Singleton
    AccountManager provideAccountManager(Context context) {
        return new AccountManager(context);
    }


    @Provides
    @Singleton
    NoteManager provideNoteManager(Context context) {
        return new NoteManager(context);
    }
}
