package com.example.achypur.notepadapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.achypur.notepadapp.DAO.UserDao;
import com.example.achypur.notepadapp.Entities.User;
import com.example.achypur.notepadapp.R;
import com.example.achypur.notepadapp.Session.SessionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

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

        final EditText login = (EditText) findViewById(R.id.login_login);
        final EditText password = (EditText) findViewById(R.id.login_password);
        Button logInButton = (Button) findViewById(R.id.login_button);
        TextView linkToSignUp = (TextView) findViewById(R.id.login_sign_up);
        SpannableString content = new SpannableString(linkToSignUp.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        linkToSignUp.setText(content);
        linkToSignUp.setTextSize(20);

        final Intent loginPage = new Intent(this, MainActivity.class);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin(login.getText().toString().trim(), password.getText().toString().trim(), userList)) {
                    mSession.createLoginSession(login.getText().toString().trim(), password.getText().toString().trim());
                    startActivity(loginPage);
                    finish();
                }
            }
        });

        final Intent signUp = new Intent(this, SignUpActivity.class);
        linkToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signUp);
                finish();
            }
        });

    }

    private boolean checkLogin(String login, String password, List<User> userList) {
        for (User someBody : userList) {
            if (someBody.getLogin().equals(login) && someBody.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

}


