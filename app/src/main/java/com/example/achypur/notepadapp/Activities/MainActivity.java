package com.example.achypur.notepadapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.achypur.notepadapp.DAO.CoordinateDao;
import com.example.achypur.notepadapp.DAO.NoteDao;
import com.example.achypur.notepadapp.DAO.PictureDao;
import com.example.achypur.notepadapp.DAO.UserDao;
import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.Entities.User;
import com.example.achypur.notepadapp.R;
import com.example.achypur.notepadapp.Session.SessionManager;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    private final static int UPLOAD_KEY = 1;

    NoteListAdapter mAdapter;
    ListView mListView;
    SessionManager mSession;
    UserDao mUserDao;
    NoteDao mNoteDao;
    CoordinateDao mCoordinateDao;
    HashMap<String, String> mCurrentUser;
    PictureDao mPictureDao;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserDao = new UserDao(this);
        mNoteDao = new NoteDao(this);
        mCoordinateDao = new CoordinateDao(this);
        mPictureDao = new PictureDao(this);


        try {
            mUserDao.open();
            mNoteDao.open();
            mCoordinateDao.open();
            mPictureDao.open();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }


        if (mUserDao.isEmpty()) {
            mUserDao.createUser("admin", "Andrii", "achyp14@gmail.com", "admin", null, null);
        }

        mSession = new SessionManager(this);
        mCurrentUser = mSession.getUserDetails();
        if (mSession.checkLogin()) {
            finish();
            return;
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT + 2:00"));
        final Date currentLocalTime = calendar.getTime();
        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT + 2:00"));

        mListView = (ListView) findViewById(R.id.note_list);
        mAdapter = new NoteListAdapter(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setList(mNoteDao.getNotesByUserId(mUserDao.findUserByLogin
                        (mCurrentUser.get(SessionManager.KEY_LOGIN)), 1));
            }
        });
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = mAdapter.getItem(position);
                Intent intent = NoteActivity.createIntentForEditNote(MainActivity.this, note.getmId());
                startActivityForResult(intent, 1);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.setItemChecked(position, true);
                return false;
            }
        });
        User user = mUserDao.findUserById(mUserDao.
                findUserByLogin(mCurrentUser.get(SessionManager.KEY_LOGIN)));
        if(user.getImage()!=null) {
            BaseActivity baseActivity = new BaseActivity();
            baseActivity.mProfilePicture.setImageBitmap(getImage(user.getImage()));
        }


        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {
            }

            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.action_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }


            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_delete:
                        SparseBooleanArray checked = mListView.getCheckedItemPositions();
                        long[] ids = mListView.getCheckedItemIds();
                        if (checked == null)
                            return false;
                        for (long id : ids) {
                            Note note = mNoteDao.getNoteById(id);
                            mCoordinateDao.deleteCoordinate(note.getmLocation());
                            mNoteDao.deleteNote(id);
                        }
                        mListView.clearChoices();
                        mAdapter.setList(mNoteDao.getNotesByUserId(mUserDao.findUserByLogin
                                (mCurrentUser.get(SessionManager.KEY_LOGIN)), 1));
                        mode.finish();
                        return true;
                    case R.id.menu_item_share:
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
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
                noteList = mNoteDao.getNotesByUserId(mUserDao.findUserByLogin
                        (mCurrentUser.get(SessionManager.KEY_LOGIN)), 1);
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
                        mAdapter.setList(mNoteDao.getNotesByUserId(mUserDao.findUserByLogin
                                (mCurrentUser.get(SessionManager.KEY_LOGIN)), 1));
                    }
                });


            }
        }
    }

    class NoteListAdapter extends BaseAdapter {
        List<Note> mNoteList;
        LayoutInflater mInflater;

        @Override
        public boolean hasStableIds() {
            return true;
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

        class ViewHolderItem {
            TextView title;
            TextView time;
            TextView sharedBy;
            View line;
            TextView location;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolderItem viewHolderItem;
            Note note = getItem(position);
            User user = mUserDao.findUserById(mUserDao.findUserByLogin
                    (mCurrentUser.get(SessionManager.KEY_LOGIN)));

            if (convertView == null || note.getmUserId() != user.getId()) {
                convertView = mInflater.inflate(R.layout.item, parent, false);
                viewHolderItem = new ViewHolderItem();
                viewHolderItem.title = (TextView) convertView.findViewById(R.id.item_title);
                viewHolderItem.time = (TextView) convertView.findViewById(R.id.time);
                viewHolderItem.sharedBy = (TextView) convertView.findViewById(R.id.item_shared_by);
                viewHolderItem.line = convertView.findViewById(R.id.item_line);
                viewHolderItem.location = (TextView) convertView.findViewById(R.id.item_location);
                convertView.setTag(viewHolderItem);
            } else {
                viewHolderItem = (ViewHolderItem) convertView.getTag();
            }
            
            if (note.getmUserId() != user.getId() && note.getmLocation() != 0) {
                viewHolderItem.sharedBy.setVisibility(View.VISIBLE);
                viewHolderItem.sharedBy.setText("Shared by " +
                        mUserDao.findUserById(note.getmUserId()).getName());
                viewHolderItem.line.setVisibility(View.VISIBLE);
                viewHolderItem.location.setVisibility(View.VISIBLE);
            } else {
                if (note.getmUserId() != user.getId()) {
                    viewHolderItem.sharedBy.setVisibility(View.VISIBLE);
                    viewHolderItem.sharedBy.setText("Shared by " +
                            mUserDao.findUserById(note.getmUserId()).getName());
                } else {
                    if (note.getmLocation() != 0) {
                        viewHolderItem.location.setVisibility(View.VISIBLE);
                    } else {
                        viewHolderItem.sharedBy.setVisibility(View.INVISIBLE);
                        viewHolderItem.line.setVisibility(View.INVISIBLE);
                        viewHolderItem.location.setVisibility(View.INVISIBLE);
                    }
                }
            }
            viewHolderItem.title.setText(note.getmTitle());
            viewHolderItem.time.setText(note.getmModifiedDate());
            return convertView;
        }
    }

    public Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}

