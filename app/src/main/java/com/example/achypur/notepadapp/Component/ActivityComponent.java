package com.example.achypur.notepadapp.Component;


import com.example.achypur.notepadapp.Module.ActivityModule;
import com.example.achypur.notepadapp.PerActivity;

import javax.inject.Singleton;

import dagger.Component;


<<<<<<< HEAD
@Singleton
@Component(modules = ActivityModule.class)
public interface ActivityComponent {
=======
@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    Activity activity();
>>>>>>> 3a2fd3db704c1e80b34c80c775e74ed3b8794240

}
