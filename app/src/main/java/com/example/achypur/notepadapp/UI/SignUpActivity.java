package com.example.achypur.notepadapp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.achypur.notepadapp.component.DaggerHomeComponent;
import com.example.achypur.notepadapp.component.HomeComponent;
import com.example.achypur.notepadapp.view.PictureConvertor;
import com.example.achypur.notepadapp.view.ProfilePicture;
import com.example.achypur.notepadapp.entities.User;
import com.example.achypur.notepadapp.managers.AccountManager;
import com.example.achypur.notepadapp.NoteApplication;
import com.example.achypur.notepadapp.module.ActivityModule;
import com.example.achypur.notepadapp.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

public class SignUpActivity extends AppCompatActivity {

    private final static int UPLOAD_KEY = 1;
    User mCurrentUser = new User();
    ProfilePicture mProfilePicture;
    Bitmap mBitmap;
    @Inject
    AccountManager mAccountManager;
    PictureConvertor mPictureConvertor;
    HomeComponent mHomeComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        mPictureConvertor = PictureConvertor.getInstance();
        component().inject(this);

        mAccountManager.initLoginSession();
        mAccountManager.createUserRepository();

        final EditText firstName = (EditText) findViewById(R.id.profile_first_name);
        final EditText login = (EditText) findViewById(R.id.profile_login);
        final EditText email = (EditText) findViewById(R.id.profile_email);
        final EditText password = (EditText) findViewById(R.id.sign_up_password);
        final EditText confirmPassword = (EditText) findViewById(R.id.profile_confirm_password);
        final Button submit = (Button) findViewById(R.id.profile_submit_button);
        final Button cancel = (Button) findViewById(R.id.profile_cancel_button);
        final Button upload = (Button) findViewById(R.id.sign_up_upload_button);
        mProfilePicture = (ProfilePicture) findViewById(R.id.sign_up_image);
        mBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.people);
        mProfilePicture.setImageBitmap(mBitmap);

        final List<User> userList = mAccountManager.findAllUsers();
        final Intent intentMain = new Intent(this, MainActivity.class);

        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (login.getText().toString().trim().equals("") |
                        firstName.getText().toString().trim().equals("") |
                        password.getText().toString().trim().equals("") |
                        confirmPassword.getText().toString().trim().equals("")) {
                    alertBuilder(Check.CHECK_INPUT);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        if (submit != null)
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login.addTextChangedListener(textWatcher);
                    firstName.addTextChangedListener(textWatcher);
                    password.addTextChangedListener(textWatcher);
                    confirmPassword.addTextChangedListener(textWatcher);
                    if (!mAccountManager.checkPassword(password.getText().toString().trim(), confirmPassword.getText().toString().trim())) {
                        alertBuilder(Check.CHECK_PASSWORD);
                    } else {
                        mCurrentUser = createUser(firstName.getText().toString().trim(),
                                login.getText().toString().trim(),
                                email.getText().toString().trim(),
                                password.getText().toString().trim(), mCurrentUser.getImage());

                        if (mAccountManager.checkUserInDb(mCurrentUser, userList)) {
                            alertBuilder(Check.CHECK_USER);
                        } else {
                            mCurrentUser = mAccountManager.createUser(mCurrentUser);
                            mAccountManager.createSession(mCurrentUser.getLogin(), mCurrentUser.getPassword());
                            intentMain.putExtra("userId", mAccountManager.getCurrentUser().getId());
                            startActivity(intentMain);
                            finish();
                        }
                    }
                }
            });

        final Intent back = new Intent(this, LoginActivity.class);
        if (cancel != null)
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(back);
                    finish();
                }
            });

        if (upload != null) {
            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                    startActivityForResult(chooserIntent, UPLOAD_KEY);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPLOAD_KEY && data != null && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();

            try {
                InputStream iStream = getContentResolver().openInputStream(selectedImage);
                byte[] image = mPictureConvertor.getBytes(iStream);
                mBitmap = mPictureConvertor.byteToBitMap(image);
                mCurrentUser.setImage(image);
                mProfilePicture.setImageBitmap(mBitmap);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public enum Check {
        CHECK_PASSWORD, CHECK_USER, CHECK_INPUT
    }

    public void alertBuilder(Check check) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog;

        if (check == Check.CHECK_INPUT) {
            builder.setMessage("Please input all fields").setPositiveButton("OK", null);
            alertDialog = builder.create();
            alertDialog.show();
        }

        if (check == Check.CHECK_PASSWORD) {
            builder.setMessage("Password is incorrect").setPositiveButton("OK", null);
            alertDialog = builder.create();
            alertDialog.show();
        }

        if (check == Check.CHECK_USER) {
            builder.setMessage("User is already exist").setPositiveButton("OK", null);
            alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public User createUser(String name, String login, String email, String password, byte[] image) {
        return new User(login, name, email, password, null, image);
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
        super.onBackPressed();
    }

    private HomeComponent component() {
        if(mHomeComponent == null) {
            mHomeComponent = DaggerHomeComponent.builder().appComponent(((NoteApplication) getApplication()).component()).activityModule(new ActivityModule(this)).build();
        }
        return mHomeComponent;
    }
}

