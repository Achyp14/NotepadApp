package com.example.achypur.notepadapp;

import android.app.Activity;
import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ArrayList<String> loginList = new ArrayList<String>();
        loginList.add("admin");
        loginList.add("user");

        final EditText editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.login_button);


        final Intent intent = new Intent();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLogin(editText.getText().toString().trim(), loginList)) {
                    boolean b = true;
                    intent.putExtra("logged",b);
                    intent.putExtra("login",editText.getText().toString());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }

            }
        });
    }

    private boolean checkLogin(String login, ArrayList<String> loginList) {
        for (String item : loginList) {
            if (login.equals(item)) {
                return true;
            }
        }
        return  false;
    }
}


