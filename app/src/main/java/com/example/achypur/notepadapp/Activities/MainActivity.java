package com.example.achypur.notepadapp.Activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.achypur.notepadapp.DAO.NoteDao;
import com.example.achypur.notepadapp.DAO.UserDao;
import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.R;
import com.example.achypur.notepadapp.Session.SessionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    NoteListAdapter mAdapter;
    ListView mListView;
    SessionManager mSession;
    UserDao mUserDao;
    NoteDao mNoteDao;
    HashMap<String, String> mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        mUserDao = new UserDao(this);
        mNoteDao = new NoteDao(this);

        try {
            mUserDao.open();
            mNoteDao.open();
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

        final Button button = (Button) findViewById(R.id.edit_button);
        final EditText title = (EditText) findViewById(R.id.edit_title);
        final EditText description = (EditText) findViewById(R.id.edit_description);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (title.getText().toString().trim().equals("") ||
                        description.getText().toString().trim().equals("")) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        title.addTextChangedListener(watcher);
        description.addTextChangedListener(watcher);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNoteDao.createNote(title.getText().toString().trim(),
                        description.getText().toString().trim(),
                        mUserDao.findUserByLogin(mCurrentUser.get(SessionManager.KEY_LOGIN)),
                        null, null, null);
                mAdapter.setList(mNoteDao.getNotesByUserId(mUserDao.findUserByLogin
                        (mCurrentUser.get(SessionManager.KEY_LOGIN))));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });

            }
        });

        mListView = (ListView) findViewById(R.id.note_list);
        mAdapter = new NoteListAdapter(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setList(mNoteDao.getNotesByUserId(mUserDao.findUserByLogin
                        (mCurrentUser.get(SessionManager.KEY_LOGIN))));
            }
        });
        final Intent intent = new Intent(this, NoteActivity.class);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView title = (TextView) view.findViewById(R.id.item_title);
                TextView description = (TextView) view.findViewById(R.id.item_description);
                Note note = mAdapter.getItem(position);
                intent.putExtra("Title", note.getmTitle());
                intent.putExtra("Content", note.getmContent());
                intent.putExtra("Id", note.getmId());
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

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(mListView.getCheckedItemCount() + " selected");
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
                    case R.id.item_delete:
                        SparseBooleanArray checked = mListView.getCheckedItemPositions();
                        long[] ids = mListView.getCheckedItemIds();
                        Log.e("Achyp", "185|MainActivity::onActionItemClicked: " + ids.length);
                        if (checked == null)
                            return false;

                        for (long id : ids) {
                            mNoteDao.deleteNote(id);
                        }
                        mListView.clearChoices();
                        mAdapter.setList(mNoteDao.getNotesByUserId(mUserDao.findUserByLogin
                                (mCurrentUser.get(SessionManager.KEY_LOGIN))));
                        mode.finish();
                        return true;
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
                        (mCurrentUser.get(SessionManager.KEY_LOGIN)));
                Collections.sort(noteList, new Comparator<Note>() {
                    @Override
                    public int compare(Note note1, Note note2) {
                        return note1.getmTitle().compareTo(note2.getmTitle());
                    }
                });
                mAdapter.setList(noteList);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.item_order_by_content:
                noteList = mNoteDao.getNotesByUserId(mUserDao.findUserByLogin
                        (mCurrentUser.get(SessionManager.KEY_LOGIN)));
                Collections.sort(noteList, new Comparator<Note>() {
                    @Override
                    public int compare(Note note1, Note note2) {
                        return note1.getmContent().compareTo(note2.getmContent());
                    }
                });
                mAdapter.setList(noteList);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.item_logout:
                mSession.logoutUser();
                finish();
            default:
                super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data.getExtras().containsKey("Title") && data.getExtras().containsKey("Content") && data.getExtras().containsKey("Id")) {
                    Note note = mNoteDao.getNoteById(data.getLongExtra("Id", 1L));
                    note.setmTitle(data.getStringExtra("Title"));
                    note.setmContent(data.getStringExtra("Content"));
                    mNoteDao.updateNote(note);
                    mAdapter.setList(mNoteDao.getNotesByUserId(mUserDao.findUserByLogin
                            (mCurrentUser.get(SessionManager.KEY_LOGIN))));
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    static class NoteListAdapter extends BaseAdapter {
        List<Note> mNoteList;
        LayoutInflater mInflater;
        HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();
        List<Long> listId = new ArrayList<Long>();

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public NoteListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public void setNewSelection(int position, boolean value) {
            mSelection.put(position, value);
            notifyDataSetChanged();
        }

        public boolean isPositionChecked(int position) {
            Boolean result = mSelection.get(position);
            return result == null ? false : result;
        }

        public void removeSelection(int position) {
            mSelection.remove(position);
            notifyDataSetChanged();
        }

        public void clearSelection() {
            mSelection = new HashMap<Integer, Boolean>();
            listId.clear();
            notifyDataSetChanged();
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
            TextView description;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolderItem viewHolderItem;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item, parent, false);
                viewHolderItem = new ViewHolderItem();
                viewHolderItem.title = (TextView) convertView.findViewById(R.id.item_title);
                viewHolderItem.description = (TextView) convertView.findViewById(R.id.item_description);
                convertView.setTag(viewHolderItem);
            } else {
                viewHolderItem = (ViewHolderItem) convertView.getTag();
            }
            Note note = getItem(position);
            viewHolderItem.title.setText(note.getmTitle());
            viewHolderItem.description.setText(note.getmContent());
            return convertView;
        }
    }
}
