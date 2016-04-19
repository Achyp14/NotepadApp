package com.example.achypur.notepadapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.achypur.notepadapp.CustomView.ProfilePicture;
import com.example.achypur.notepadapp.DAO.UserDao;
import com.example.achypur.notepadapp.Entities.User;
import com.example.achypur.notepadapp.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private final static int UPLOAD_KEY = 1;
    UserDao mUserDao;
    User mCurrentUser = new User();
    ProfilePicture mProfilePicture;
    Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mUserDao = new UserDao(this);
        try {
            mUserDao.open();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        final EditText firstName = (EditText) findViewById(R.id.sign_up_first_name);
        final EditText login = (EditText) findViewById(R.id.sign_up_login);
        final EditText email = (EditText) findViewById(R.id.sign_up_email);
        final EditText password = (EditText) findViewById(R.id.sign_up_password);
        final EditText confirmPassword = (EditText) findViewById(R.id.sign_up_confirm_password);
        Button submit = (Button) findViewById(R.id.sign_up_submit_button);
        Button cancel = (Button) findViewById(R.id.sign_up_cancel_button);
        Button upload = (Button) findViewById(R.id.sign_up_upload_button);
        //mImageView = (ImageView) findViewById(R.id.image);
        mProfilePicture = (ProfilePicture) findViewById(R.id.sign_up_image);
        mBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.people);
        mProfilePicture.setImageBitmap(mBitmap);
        //mImageView.setImageBitmap(getCircularBitmapWithWhiteBorder(mBitmap,15));


        final List<User> userList = mUserDao.getAllUsers();
        final Intent intent = new Intent(this, MainActivity.class);

        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(login.getText().toString().trim().equals("") |
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.addTextChangedListener(textWatcher);
                firstName.addTextChangedListener(textWatcher);
                password.addTextChangedListener(textWatcher);
                confirmPassword.addTextChangedListener(textWatcher);
                if (!checkPassword(password.getText().toString().trim(), confirmPassword.getText().toString().trim())) {
                    alertBuilder(Check.CHECK_PASSWORD);
                } else {
                    mCurrentUser = createUser(firstName.getText().toString().trim(),
                            login.getText().toString().trim(),
                            email.getText().toString().trim(),
                            password.getText().toString().trim());

                    if (checkUserInDb(mCurrentUser, userList)) {
                        alertBuilder(Check.CHECK_USER);
                    } else {
                        createUserInDb(mCurrentUser);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        final Intent back = new Intent(this, LoginActivity.class);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(back);
                finish();
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPLOAD_KEY && data != null && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();

            try {
                InputStream iStream = getContentResolver().openInputStream(selectedImage);
                mBitmap = getImage(getBytes(iStream));
                mCurrentUser.setImage(getBytes(iStream));
                mProfilePicture.setImageBitmap(mBitmap);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Bitmap getCircularBitmapWithWhiteBorder(Bitmap bitmap,
                                                          int borderWidth) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        final int width = bitmap.getWidth() + borderWidth;
        final int height = bitmap.getHeight() + borderWidth;

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);
        float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2, paint);
        return canvasBitmap;
    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public enum Check {
        CHECK_PASSWORD, CHECK_USER, CHECK_INPUT
    }

    public void alertBuilder(Check check) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog;

        if (check == Check.CHECK_INPUT) {
            builder.setMessage("Please input all field").setPositiveButton("OK", null);
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

    public boolean checkPassword(String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkUserInDb(User user, List<User> userList) {
        for (User human : userList) {
            if (human.getLogin().equals(user.getLogin())) {
                return true;
            }
        }
        return false;
    }

    public User createUser(String name, String login, String email, String password) {
        return new User(login, name, email, password, null, null);
    }

    public User createUserInDb(User user) {
        User currentUser = mUserDao.createUser(user);
        return currentUser;
    }
}

