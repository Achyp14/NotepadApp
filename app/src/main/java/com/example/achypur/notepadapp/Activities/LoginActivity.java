package com.example.achypur.notepadapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.achypur.notepadapp.Entities.User;
import com.example.achypur.notepadapp.R;
import com.example.achypur.notepadapp.Session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final SessionManager mSession;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSession = new SessionManager(this);
        final List<User> userList = new ArrayList<>();

        final EditText editText = (EditText) findViewById(R.id.login_login);
        Button button = (Button) findViewById(R.id.login_button);

        final Intent intent = new Intent(this, MainActivity.class);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin(editText.getText().toString().trim(), userList)) {
                    mSession.createLoginSession(editText.getText().toString().trim());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean checkLogin(String login, List<User> userList) {
        for (User someBody : userList) {
            if (someBody.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }
}


