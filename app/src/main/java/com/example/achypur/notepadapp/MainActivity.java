package com.example.achypur.notepadapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    List<Note> mNotes = new ArrayList<Note>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final NoteListAdapter mAdapter = new NoteListAdapter(this);

        mNotes.add(new Note("1", "2"));

        Button button = (Button) findViewById(R.id.edit_button);
        final EditText mTitle = (EditText) findViewById(R.id.edit_title);
        final EditText mDescription = (EditText) findViewById(R.id.edit_description);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitle.getText().toString().trim().equals("") ||
                        mDescription.getText().toString().trim().equals("")) {
                    return;
                }

                mNotes.add(new Note(mTitle.getText().toString(), mDescription.getText().toString()));
                mAdapter.setList(mNotes);
            }
        });
        ListView listView = (ListView) findViewById(R.id.note_list);
        mAdapter.setList(mNotes);
        listView.setAdapter(mAdapter);
    }

    static class NoteListAdapter extends BaseAdapter {
        List<Note> mNoteList;
        LayoutInflater mInflater;

        public NoteListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public NoteListAdapter(Context context, List<Note> list) {
            this(context);
            mNoteList = list;
        }

        public void setList(List<Note> list) {
            mNoteList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mNoteList.size();
        }

        @Override
        public Note getItem(int position) {
            return mNoteList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            TextView title;
            TextView description;
            view = mInflater.inflate(R.layout.item, parent, false);
            Note note = getItem(position);

            title = (TextView) view.findViewById(R.id.item_title);
            description = (TextView) view.findViewById(R.id.item_description);

            title.setText(note.getTitle());
            description.setText(note.getDescription());

            return view;
        }
    }


}
