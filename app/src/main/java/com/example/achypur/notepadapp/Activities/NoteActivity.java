package com.example.achypur.notepadapp.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.achypur.notepadapp.DAO.NoteDao;
import com.example.achypur.notepadapp.DAO.UserDao;
import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.R;
import com.example.achypur.notepadapp.Session.SessionManager;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class NoteActivity extends AppCompatActivity {
    private final static String NOTE_ID_KEY = "id";

    NoteDao mNoteDao;
    UserDao mUserDao;
    Note mNote = null;
    HashMap<String, String> mCurrentUser;
    SessionManager mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.note_toolbar);
        setSupportActionBar(toolbar);

        mNoteDao = new NoteDao(this);
        mUserDao = new UserDao(this);
        mSession = new SessionManager(this);
        mCurrentUser = mSession.getUserDetails();

        try {
            mNoteDao.open();
            mUserDao.open();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        final EditText title = (EditText) findViewById(R.id.note_edit_title);
        final EditText content = (EditText) findViewById(R.id.note_edit_content);
        final TextView time = (TextView) findViewById(R.id.note_edit_time);

        final Button save = (Button) findViewById(R.id.note_button_submit);
        final Button cancel = (Button) findViewById(R.id.note_button_cancel);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT + 2:00"));
        final Date currentLocalTime = calendar.getTime();
        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT + 2:00"));
        time.setText(dateFormat.format(currentLocalTime));
        save.setEnabled(false);


        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (title.getText().toString().trim().equals("") ||
                        content.getText().toString().trim().equals("")) {
                    save.setEnabled(false);
                } else {
                    save.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        title.addTextChangedListener(watcher);
        content.addTextChangedListener(watcher);

        initParamsFromIntent(getIntent());
        if (isEditMode()) {
            title.setText(mNote.getmTitle());
            content.setText(mNote.getmContent());
        }


        final Intent intent = new Intent();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode()) {
                    mNote.setmTitle(title.getText().toString().trim());
                    mNote.setmContent(content.getText().toString().trim());
                    mNote.setmModifiedDate(dateFormat.format(currentLocalTime));
                    mNoteDao.updateNote(mNote);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    mNoteDao.createNote(title.getText().toString().trim(),
                            content.getText().toString().trim(), mUserDao.findUserByLogin
                                    (mCurrentUser.get(SessionManager.KEY_LOGIN)),
                            dateFormat.format(currentLocalTime),
                            dateFormat.format(currentLocalTime), false);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        final Intent mainActivity = new Intent(this, MainActivity.class);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mainActivity);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        if (isEditMode()) {
            menu.findItem(R.id.note_menu_check_shared).setVisible(true).
                    setChecked(mNote.getmPolicyStatus());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
        AlertDialog alertDialog;
        switch (item.getItemId()) {
            case R.id.note_menu_check_shared:
                if (!item.isChecked()) {
                    aBuilder.setMessage("Make this note public?").setCancelable(true)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mNote.setmPolicyId(true);
                                    item.setChecked(true);
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    alertDialog = aBuilder.create();
                    alertDialog.show();
                } else {
                    mNote.setmPolicyId(false);
                    item.setChecked(false);
                }
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean isEditMode() {
        return mNote != null;
    }

    private void initParamsFromIntent(Intent intent) {
        mNote = null;
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(NOTE_ID_KEY)) {
            mNote = mNoteDao.getNoteById(intent.getLongExtra(NOTE_ID_KEY, -1));

        }
    }

    public static Intent createIntentForAddNote(Context context) {
        Intent intent = new Intent(context, NoteActivity.class);
        return intent;
    }

    public static Intent createIntentForEditNote(Context context, Long id) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(NOTE_ID_KEY, id);
        return intent;
    }


}
