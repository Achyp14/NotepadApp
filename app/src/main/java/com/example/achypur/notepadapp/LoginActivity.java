package com.example.achypur.notepadapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final SessionManager mSession;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSession = new SessionManager(this);
        final List<User> userList = MainActivity.fillInDB(this);
        Log.i(TAG, userList.get(0).getLogin());
        Log.i(TAG, userList.get(1).getLogin());
        Log.i(TAG, userList.get(2).getLogin());


        final EditText editText = (EditText) findViewById(R.id.editText);
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


