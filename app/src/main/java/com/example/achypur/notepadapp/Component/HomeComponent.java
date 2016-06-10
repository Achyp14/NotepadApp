package com.example.achypur.notepadapp.component;

import com.example.achypur.notepadapp.module.ActivityModule;
import com.example.achypur.notepadapp.module.PerActivity;
import com.example.achypur.notepadapp.ui.BaseActivity;
import com.example.achypur.notepadapp.ui.LoginActivity;
import com.example.achypur.notepadapp.ui.MainActivity;
import com.example.achypur.notepadapp.ui.NoteActivity;
import com.example.achypur.notepadapp.ui.ProfileActivity;
import com.example.achypur.notepadapp.ui.SignUpActivity;


import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface HomeComponent extends ActivityComponent {
    void inject(LoginActivity activity);

    void inject(SignUpActivity activity);

    void inject(MainActivity activity);

    void inject(NoteActivity activity);

    void inject(BaseActivity activity);

    void inject(ProfileActivity activity);

}
