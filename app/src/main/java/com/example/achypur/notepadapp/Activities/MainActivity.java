package com.example.achypur.notepadapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achypur.notepadapp.DAO.CoordinateDao;
import com.example.achypur.notepadapp.DAO.NoteDao;
import com.example.achypur.notepadapp.DAO.PictureDao;
import com.example.achypur.notepadapp.DAO.UserDao;
import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.Entities.User;
import com.example.achypur.notepadapp.R;
import com.example.achypur.notepadapp.Session.SessionManager;


import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends BaseActivity {

    NoteListAdapter mAdapter;
    ListView mListView;
    SessionManager mSession;
    UserDao mUserDao;
    NoteDao mNoteDao;
    CoordinateDao mCoordinateDao;
    HashMap<String, String> mCurrentUser;
    PictureDao mPictureDao;
    User mLoggedUser = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserDao = new UserDao(this);
        mNoteDao = new NoteDao(this);
        mCoordinateDao = new CoordinateDao(this);
        mPictureDao = new PictureDao(this);
        mSession = new SessionManager(this);
        mCurrentUser = mSession.getUserDetails();

        try {
            mUserDao.open();
            mNoteDao.open();
            mCoordinateDao.open();
            mPictureDao.open();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Long id = (Long) extras.get("userId");
            mLoggedUser = mUserDao.findUserById(id);
        } else {
            mLoggedUser = mUserDao.findUserById(mUserDao.findUserByLogin(mCurrentUser.get(SessionManager.KEY_LOGIN)));
        }


        setContentView(R.layout.activity_main);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT + 2:00"));
        final Date currentLocalTime = calendar.getTime();
        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT + 2:00"));

        mListView = (ListView) findViewById(R.id.note_list);
        mAdapter = new NoteListAdapter(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setList(mNoteDao.getNotesByUserId(mLoggedUser.getId(), 1));
                for(Note note : mNoteDao.getNotesByUserId(mLoggedUser.getId(), 1)) {
                    Log.e("Achyp", "98|MainActivity::run: " + note.getmId());
                }
            }
        });
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = mAdapter.getItem(position);
                Intent intent = NoteActivity.createIntentForReviseNote(MainActivity.this, note.getmId(), true);
                startActivityForResult(intent, 1);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.setItemChecked(position,true);
                return false;
            }
        });

        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {
            }

            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                mAdapter.setMultiChoice(true);
                mode.getMenuInflater().inflate(R.menu.action_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return true;
            }


            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_delete:
                        SparseBooleanArray checked = mListView.getCheckedItemPositions();
                        long[] ids = new long[checked.size()];
                        for (int i = 0; i < checked.size(); i++) {
                            int key = checked.keyAt(i);
                            if(checked.get(key)) {
                                ids[i] = mListView.getItemIdAtPosition(key);
                            }
                        }
                        if (checked.size() == 0)
                            return false;
                        for (long id : ids) {
                            if(id != 0) {
                                Note note = mNoteDao.getNoteById(id);
                                mCoordinateDao.deleteCoordinate(note.getmLocation());
                                mNoteDao.deleteNote(id);
                            } else {
                                return false;
                            }

                        }
                        mListView.clearChoices();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.setList(mNoteDao.getNotesByUserId(mLoggedUser.getId(), 1));
                            }
                        });
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                mAdapter.setMultiChoice(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<Note> noteList;
        switch (item.getItemId()) {
            case (R.id.item_order_by_title):
                noteList = mNoteDao.getNotesByUserId(mLoggedUser.getId(), 1);
                Collections.sort(noteList, new Comparator<Note>() {
                    @Override
                    public int compare(Note note1, Note note2) {
                        return note1.getmTitle().compareTo(note2.getmTitle());
                    }
                });
                mAdapter.setList(noteList);
                return true;
            case R.id.item_add_note:
                Intent intent = NoteActivity.createIntentForAddNote(this);
                startActivityForResult(intent, 1);
                return true;
            default:
                super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setList(mNoteDao.getNotesByUserId(mLoggedUser.getId(), 1));
                    }
                });
            }
        }
    }

    private static class ViewHolderItem {
        TextView title;
        TextView time;
        TextView sharedBy;
        View line;
        TextView location;

        public ViewHolderItem(View view) {
            title = (TextView) view.findViewById(R.id.item_title);
            time = (TextView) view.findViewById(R.id.time);
            sharedBy = (TextView) view.findViewById(R.id.item_shared_by);
            location = (TextView) view.findViewById(R.id.item_location);
            line = view.findViewById(R.id.item_line);
        }
    }

    class NoteListAdapter extends BaseAdapter {
        List<Note> mNoteList;
        LayoutInflater mInflater;
        boolean multiChoice = false;

        public void setMultiChoice(boolean value) {
            multiChoice = value;
        }

        @Override
        public boolean isEnabled(int position) {
            if (!multiChoice)
                return true;


            boolean enabled = getItem(position).getmUserId() == mLoggedUser.getId();
            if (!enabled) {
                Toast.makeText(MainActivity.this, "You can change only your notes", Toast.LENGTH_SHORT).show();
            }

            return enabled;
        }

        public NoteListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
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
            return getItem(position).getmId();
        }

        private void bind(ViewHolderItem holder, Note note) {
            if (note.getmUserId() != mLoggedUser.getId()) {
                holder.location.setVisibility(View.GONE);
                holder.sharedBy.setVisibility(View.VISIBLE);
                holder.sharedBy.setText("Shared by " +
                        mUserDao.findUserById(note.getmUserId()).getName());
            } else {
                holder.location.setVisibility(View.GONE);
                holder.sharedBy.setVisibility(View.GONE);
            }

            if (note.getmLocation() != 0) {
                holder.location.setVisibility(View.VISIBLE);
            }

            holder.title.setText(note.getmTitle());
            holder.time.setText(note.getmModifiedDate());
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolderItem viewHolderItem;
            Note note = getItem(position);


            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item, parent, false);
                viewHolderItem = new ViewHolderItem(convertView);
                convertView.setTag(viewHolderItem);
            } else {
                viewHolderItem = (ViewHolderItem) convertView.getTag();
            }

            bind(viewHolderItem, note);



            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

