package com.example.achypur.notepadapp.Component;

        import android.app.Activity;
        import android.content.Context;

        import com.example.achypur.notepadapp.Managers.AccountManager;
        import com.example.achypur.notepadapp.Managers.NoteManager;
        import com.example.achypur.notepadapp.Module.ActivityModule;
        import com.example.achypur.notepadapp.PerActivity;

        import javax.inject.Singleton;

        import dagger.Component;

@PerActivity
@Singleton
@Component(modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(Context context);

    Context context();

    NoteManager noteManager();

    AccountManager accountManager();
}
