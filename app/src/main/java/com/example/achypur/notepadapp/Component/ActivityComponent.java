package com.example.achypur.notepadapp.component;


import android.app.Activity;

import com.example.achypur.notepadapp.module.ActivityModule;
import com.example.achypur.notepadapp.module.PerActivity;

import dagger.Component;


@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    Activity activity();
}
