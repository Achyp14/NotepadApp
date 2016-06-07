package com.example.achypur.notepadapp.Component;

import android.app.Activity;

import com.example.achypur.notepadapp.Module.ActivityModule;
import com.example.achypur.notepadapp.PerActivity;
import com.example.achypur.notepadapp.UI.BaseActivity;
import com.example.achypur.notepadapp.UI.LoginActivity;
import com.example.achypur.notepadapp.UI.MainActivity;
import com.example.achypur.notepadapp.UI.NoteActivity;
import com.example.achypur.notepadapp.UI.ProfileActivity;
import com.example.achypur.notepadapp.UI.SignUpActivity;


import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface HomeComponent extends ActivityComponent{
    void inject(LoginActivity activity);

    void inject(SignUpActivity activity);

    void inject(MainActivity activity);

    void inject(NoteActivity activity);

    void inject(BaseActivity activity);

    void inject(ProfileActivity activity);

}
