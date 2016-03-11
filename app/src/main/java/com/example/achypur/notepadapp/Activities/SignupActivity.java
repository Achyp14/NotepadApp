package com.example.achypur.notepadapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.achypur.notepadapp.DAO.UserDao;
import com.example.achypur.notepadapp.Entities.User;
import com.example.achypur.notepadapp.R;

import java.net.URI;
import java.sql.SQLException;

public class SignUpActivity extends AppCompatActivity {

    private  final  static  int UPLOAD_KEY = 1;
    ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final UserDao userDao = new UserDao(this);
        try {
            userDao.open();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        User user;
        final EditText name = (EditText) findViewById(R.id.sign_up_edit_name);
        final EditText login = (EditText) findViewById(R.id.sign_up_edit_login);
        final EditText email = (EditText) findViewById(R.id.sign_up_edit_email);
        final EditText password = (EditText) findViewById(R.id.sign_up_edit_password);
        final EditText confirm = (EditText) findViewById(R.id.sign_up_edit_conf_password);
        final Button submit = (Button) findViewById(R.id.sign_up_submit);
        final Button upload = (Button) findViewById(R.id.sign_up_download);
        final Intent intent = new Intent(this, MainActivity.class);
        profilePicture = (ImageView) findViewById(R.id.profile_picture);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (name.getText().toString().trim().equals("") ||
                        login.getText().toString().trim().equals("") ||
                        email.getText().toString().trim().equals("") ||
                        password.getText().toString().trim().equals("") ||
                        confirm.getText().toString().trim().equals("") ||
                        !password.getText().toString().trim().equals(confirm.getText().toString().trim())) {
                } else {
                    submit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        name.addTextChangedListener(textWatcher);
        login.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        confirm.addTextChangedListener(textWatcher);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDao.createUser(login.getText().toString(), name.getText().toString(), email.getText().toString(), password.getText().toString(), null, null);
                startActivity(intent);
                finish();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, UPLOAD_KEY);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == UPLOAD_KEY && data != null && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            profilePicture.setImageURI(selectedImage);

        }
    }
}

