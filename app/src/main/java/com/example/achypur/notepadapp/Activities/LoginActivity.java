package com.example.achypur.notepadapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.achypur.notepadapp.DAO.UserDao;
import com.example.achypur.notepadapp.Entities.User;
import com.example.achypur.notepadapp.R;
import com.example.achypur.notepadapp.Session.SessionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserDao userDao = new UserDao(this);
        try {
            userDao.open();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        final SessionManager mSession;
        mSession = new SessionManager(this);
        final List<User> userList = userDao.getAllUsers();

        final EditText editText = (EditText) findViewById(R.id.login_login);
        Button logInButton = (Button) findViewById(R.id.login_button);
        Button signUpButton = (Button) findViewById(R.id.sign_up_button);

        final Intent login = new Intent(this, MainActivity.class);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin(editText.getText().toString().trim(), userList)) {
                    mSession.createLoginSession(editText.getText().toString().trim());
                    startActivity(login);
                    finish();
                }
            }
        });

        final Intent signUp = new Intent(this, SignUpActivity.class);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signUp);
                finish();
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


