package com.example.achypur.notepadapp.Component;


import com.example.achypur.notepadapp.Module.ActivityModule;
import com.example.achypur.notepadapp.PerActivity;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = ActivityModule.class)
public interface ActivityComponent {

}
