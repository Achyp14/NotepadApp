package com.example.achypur.notepadapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.achypur.notepadapp.R;

public class NoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        final EditText title = (EditText) findViewById(R.id.note_edit_title);
        final EditText content = (EditText) findViewById(R.id.note_edit_content);
        final Long id;
        Button button = (Button) findViewById(R.id.note_button_submit);

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            title.setText(extras.getString("Title"));
            content.setText(extras.getString("Content"));
            id = extras.getLong("Id");
        } else {
            id = null;
        }

        final Intent intent = new Intent();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Achyp", "34|NoteActivity::onClick: "+ title.getText().toString().trim());
                intent.putExtra("Title",title.getText().toString().trim());
                intent.putExtra("Content", content.getText().toString().trim());
                intent.putExtra("Id",id);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

}
