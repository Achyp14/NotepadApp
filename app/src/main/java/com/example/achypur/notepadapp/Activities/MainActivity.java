package com.example.achypur.notepadapp.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends BaseActivity {

    NoteListAdapter mNoteAdapter;
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

        mListView = (ListView) findViewById(R.id.note_list);
        mNoteAdapter = new NoteListAdapter(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNoteAdapter.setList(mNoteDao.getNotesByUserId(mLoggedUser.getId(), 1));
            }
        });
        mListView.setAdapter(mNoteAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = mNoteAdapter.getItem(position);
                Intent intent = NoteActivity.createIntentForReviseNote(MainActivity.this, note.getmId(), true);
                startActivityForResult(intent, 1);
            }
        });

        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int pos = -1;
            SparseBooleanArray selectedItems = new SparseBooleanArray();
            List<Integer> positionList = new ArrayList<>();

            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {
                pos = position;

                if (checked) {
                    selectedItems.append(position, checked);
                    positionList.add(pos);
                } else {
                    selectedItems.delete(position);
                    positionList.remove(positionList.indexOf(pos));
                }

                if (selectedItems.size() > 1) {
                    mode.getMenu().findItem(R.id.menu_item_edit).setVisible(false);
                } else {
                    mode.getMenu().findItem(R.id.menu_item_edit).setVisible(true);
                }
            }

            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                mNoteAdapter.setMultiChoice(true);
                mode.getMenuInflater().inflate(R.menu.action_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }


            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                Note note;
                switch (item.getItemId()) {
                    case R.id.menu_item_share:
                        List<Note> noteList = new ArrayList<>();
                        for (Integer position: positionList) {
                            noteList.add(mNoteAdapter.getItem(position));
                        }
                        alertForSharing(noteList);
                        mode.finish();
                        return  true;

                    case R.id.menu_item_edit:
                        note = mNoteAdapter.getItem(pos);
                        if (mLoggedUser.getId() != note.getmUserId()) {
                            Toast.makeText(MainActivity.this, "You can't modify this note", Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            Intent intent = NoteActivity.createIntentForEditNote(MainActivity.this, note.getmId());
                            startActivityForResult(intent, 1);
                            return true;
                        }
                    case R.id.menu_item_delete:
                        long[] ids = new long[selectedItems.size()];
                        for (int i = 0; i < selectedItems.size(); i++) {
                            int key = selectedItems.keyAt(i);
                            if (selectedItems.get(key)) {
                                ids[i] = mListView.getItemIdAtPosition(key);
                            }
                        }
                        if (selectedItems.size() == 0)
                            return false;
                        for (long id : ids) {
                            if (id != 0) {
                                note = mNoteDao.getNoteById(id);
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
                                mNoteAdapter.setList(mNoteDao.getNotesByUserId(mLoggedUser.getId(), 1));
                            }
                        });
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                mNoteAdapter.setMultiChoice(false);
            }
        });
    }

    public void alertForSharing(final List<Note> list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog;
        String message;
        if (!list.isEmpty()) {
            if(list.size() == 1) {
                message= "Share this note?";
            } else {
                message = "Share picked notes?";
            }

            alertDialog = builder.setTitle(message).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for(Note note : list) {
                        note.setmPolicyStatus(true);
                        mNoteDao.updateNote(note);
                    }
                }
            }).setNegativeButton("NO", null).create();
            alertDialog.show();
        }
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
                mNoteAdapter.setList(noteList);
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
                        mNoteAdapter.setList(mNoteDao.getNotesByUserId(mLoggedUser.getId(), 1));
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

