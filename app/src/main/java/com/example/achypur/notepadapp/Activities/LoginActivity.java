package com.example.achypur.notepadapp.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achypur.notepadapp.DAO.UserDao;
import com.example.achypur.notepadapp.Entities.User;
import com.example.achypur.notepadapp.R;
import com.example.achypur.notepadapp.Session.SessionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    User mUser;
    HashMap<String, String> mCurrentUser = new HashMap<>();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        checkAndRequestPermissions();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final UserDao userDao = new UserDao(this);
        try {
            userDao.open();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        final SessionManager mSession;
        mSession = new SessionManager(this);
        final List<User> userList = userDao.getAllUsers();


        if (userDao.isEmpty()) {
            mUser = userDao.createUser("admin", "Andrii", "achyp14@gmail.com", "admin", null, null);
            mSession.createLoginSession(mUser.getLogin(), mUser.getPassword());
            mSession.logoutUser();
            finish();
        }

        final EditText login = (EditText) findViewById(R.id.login_login);
        final EditText password = (EditText) findViewById(R.id.login_password);
        Button logInButton = (Button) findViewById(R.id.login_button);
        TextView linkToSignUp = (TextView) findViewById(R.id.login_sign_up);
        SpannableString content = new SpannableString(linkToSignUp.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        linkToSignUp.setText(content);
        linkToSignUp.setTextSize(20);
        final Intent loginPage = new Intent(this, MainActivity.class);
        mCurrentUser = mSession.getUserDetails();

        if (mSession.isLoggedIn()) {
            mUser = userDao.findUserById(userDao.findUserByLogin(mCurrentUser.get(SessionManager.KEY_LOGIN)));
            loginPage.putExtra("userId", mUser.getId());
            startActivity(loginPage);
            finish();
        }

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin(login.getText().toString().trim(), password.getText().toString().trim(), userList)) {
                    mSession.createLoginSession(login.getText().toString().trim(), password.getText().toString().trim());
                    mUser = userDao.findUserById(userDao.findUserByLogin(login.getText().toString()));
                    loginPage.putExtra("userId", mUser.getId());
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
        Toast.makeText(this, "Bad credentials", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void checkAndRequestPermissions() {
        int mediaPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (mediaPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                if (Arrays.asList(grantResults).contains(PackageManager.PERMISSION_DENIED) ) {
                    //return
                }

                //Sok
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("Media and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


}


