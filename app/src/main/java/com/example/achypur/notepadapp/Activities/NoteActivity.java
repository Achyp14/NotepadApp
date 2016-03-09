package com.example.achypur.notepadapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.achypur.notepadapp.DAO.NoteDao;
import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.R;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class NoteActivity extends AppCompatActivity {

    NoteDao mNoteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.note_toolbar);
        setSupportActionBar(toolbar);

        mNoteDao = new NoteDao(this);

        try {
            mNoteDao.open();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        final EditText title = (EditText) findViewById(R.id.note_edit_title);
        final EditText content = (EditText) findViewById(R.id.note_edit_content);
        final TextView time = (TextView) findViewById(R.id.note_edit_time);
        final Long id;
        Note note;
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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            save.setEnabled(true);
            id = extras.getLong("Id");
            note = mNoteDao.getNoteById(id);
            title.setText(note.getmTitle());
            content.setText(note.getmContent());
            time.setText(mNoteDao.getNoteById(id).getmModifiedDate());
        } else {
            id = Long.valueOf(0);
        }

        final Intent intent = new Intent();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Title", title.getText().toString().trim());
                intent.putExtra("Content", content.getText().toString().trim());
                intent.putExtra("Id", id);
                intent.putExtra("modifiedDate", dateFormat.format(currentLocalTime));
                setResult(RESULT_OK, intent);
                finish();
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
