package com.example.achypur.notepadapp.UI;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achypur.notepadapp.Application.NoteApplication;
import com.example.achypur.notepadapp.DAO.CoordinateDao;
import com.example.achypur.notepadapp.DAO.ForecastDao;
import com.example.achypur.notepadapp.DAO.PictureDao;
import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.Entities.Picture;
import com.example.achypur.notepadapp.Entities.User;
import com.example.achypur.notepadapp.Managers.AccountManager;
import com.example.achypur.notepadapp.Managers.NoteManager;
import com.example.achypur.notepadapp.R;
import com.example.achypur.notepadapp.Session.SessionManager;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity {

    NoteListAdapter mNoteAdapter;
    ListView mListView;
    SessionManager mSession;
    CoordinateDao mCoordinateDao;
    HashMap<String, String> mCurrentUser;
    PictureDao mPictureDao;
    User mLoggedUser = new User();
    SearchView mSearchView;
    List<Note> mNotesList;
    ForecastDao mForecastDao;
    MatrixCursor mCursor;
//    NoteCursorAdapter mNoteCursorAdapter;

    AccountManager mAccountManager;
    NoteManager mNoteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCoordinateDao = new CoordinateDao(this);
        mPictureDao = new PictureDao(this);
        mForecastDao = new ForecastDao(this);
        mSession = new SessionManager(this);
        mCurrentUser = mSession.getUserDetails();

        mAccountManager = NoteApplication.getsAccountManager();
        mAccountManager.initLoginSession();
        mAccountManager.createUserRepository();
        mCursor = new MatrixCursor(new String[]{"_id","title","content","user_id","date_of_creation","date_of_last_modified","policy","coordinates_id"});

        mNoteManager = NoteApplication.getsNoteManager();
        mNoteManager.createNoteRepo();
        try {
            mCoordinateDao.open();
            mPictureDao.open();
            mForecastDao.open();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Long id = (Long) extras.get("userId");
            mLoggedUser = mAccountManager.findUserById(id);
        } else {
            mLoggedUser = mAccountManager.findUserById(mAccountManager.findUserId(mAccountManager.retrieveLogin()));
        }

        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.note_list);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.note_float_button);
        if (floatingActionButton != null) {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = NoteActivity.createIntentForAddNote(MainActivity.this);
                    startActivity(intent);
                    finish();
                }
            });
        }
        mNoteAdapter = new NoteListAdapter(this);
        for (Note note : mNoteManager.findAll(mLoggedUser.getId(),1)) {
            Log.e("112","note" + note.getmTitle());
        }
        mNotesList = mNoteManager.findAll(mLoggedUser.getId(), 1);

        for (Note item : mNotesList) {
            mCursor.addRow(new Object[]{item.getmId(),item.getmTitle(),item.getmContent(), item.getmUserId(), item.getmCreatedDate(), item.getmModifiedDate(), item.getmPolicyStatus(), item.getmLocation()});
        }
//        mNoteCursorAdapter= new NoteCursorAdapter(this, mCursor, mNotesList);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNoteAdapter.setList(mNotesList);
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
                return true;
            }


            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                Note note;
                switch (item.getItemId()) {
                    case R.id.menu_item_share:
                        List<Note> noteList = new ArrayList<>();
                        for (Integer position : positionList) {
                            noteList.add(mNoteAdapter.getItem(position));
                        }
                        alertForSharing(noteList);
                        mode.finish();
                        return true;

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
                                note = mNoteManager.findNote(id);
                                mCoordinateDao.deleteCoordinate(note.getmLocation());
                                mNoteManager.deleteNote(id);
                            } else {
                                return false;
                            }
                        }
                        mListView.clearChoices();
                        mListView.invalidate();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mNoteAdapter.setList(mNoteManager.findAll(mLoggedUser.getId(), 1));
                            }
                        });

                        for (Note item1 : mNoteManager.findAll(mLoggedUser.getId(),1)) {
                            Log.e("222","note " + item1.getmTitle());
                        }
                        mListView.setAdapter(mNoteAdapter);
//                        mNoteCursorAdapter.notifyDataSetChanged();
                        mSearchView.invalidate();
                        getSupportActionBar().invalidateOptionsMenu();
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                mNoteAdapter.setMultiChoice(false);
                positionList.clear();
            }
        });

    }

    public void alertForSharing(final List<Note> list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog;
        String message;
        if (!list.isEmpty()) {
            if (list.size() == 1) {
                message = "Share this note?";
            } else {
                message = "Share picked notes?";
            }

            alertDialog = builder.setTitle(message).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (Note note : list) {
                        note.setmPolicyStatus(true);
                        mNoteManager.updateNote(note);
                    }
                }
            }).setNegativeButton("NO", null ).create();
            alertDialog.show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.item_search_note));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        mSearchView.setQueryHint("Search");
        mSearchView.setBackgroundColor(Color.WHITE);

        for (Note item1 : mNoteManager.findAll(mLoggedUser.getId(),1)) {
            Log.e("276","note " + item1.getmTitle());
        }
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mNoteAdapter.filter(newText);
                mListView.invalidate();
                return true;
            }

        });

        return true;
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
                noteList = mNoteManager.findAll(mLoggedUser.getId(), 1);
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
            case R.id.item_search_note:
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
                        mNoteAdapter.setList(mNoteManager.findAll(mLoggedUser.getId(), 1));
                    }
                });
            }
        }
    }

    private static class ViewHolderItem {
        TextView title;
        TextView time;
        TextView sharedBy;
        ImageView location;
        ImageView picture;
        ImageView weather;

        public ViewHolderItem(View view) {
            title = (TextView) view.findViewById(R.id.item_title);
            time = (TextView) view.findViewById(R.id.time);
            sharedBy = (TextView) view.findViewById(R.id.item_shared_by);
            location = (ImageView) view.findViewById(R.id.item_location);
            picture = (ImageView) view.findViewById(R.id.item_picture);
            weather = (ImageView) view.findViewById(R.id.item_weather);
        }
    }

    class NoteListAdapter extends BaseAdapter {
        List<Note> mAdapterList;
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
            mAdapterList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mAdapterList.size();
        }

        @Override
        public Note getItem(int position) {
            return mAdapterList.get(position);
        }


        @Override
        public long getItemId(int position) {
            return getItem(position).getmId();
        }

        private void bind(ViewHolderItem holder, Note note) {
            Picture picture;
            boolean weather = mForecastDao.ifExistForecastForNote(note.getmId());

            Long id = mPictureDao.findPictureByNoteId(note.getmId());

            if (id != null) {
                picture = mPictureDao.findPictureById(id);

            } else {
                picture = null;
            }

            if (note.getmUserId() != mLoggedUser.getId()) {
                String shared = "Shared by " +
                       mAccountManager.findUserById(note.getmUserId()).getName();
                holder.location.setVisibility(View.GONE);
                holder.sharedBy.setVisibility(View.VISIBLE);
                holder.sharedBy.setText(shared);
            } else {
                holder.location.setVisibility(View.GONE);
                holder.sharedBy.setVisibility(View.GONE);
            }

            if (note.getmLocation() != 0) {
                holder.location.setVisibility(View.VISIBLE);
            }

            if (picture != null) {
                holder.picture.setVisibility(View.VISIBLE);
            }

            if (weather) {
               holder.weather.setVisibility(View.VISIBLE);
            }

            holder.title.setText(note.getmTitle());
            holder.time.setText(note.getmModifiedDate());
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolderItem viewHolderItem;
            Note note = getItem(position);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_note_list, parent, false);
                viewHolderItem = new ViewHolderItem(convertView);
                convertView.setTag(viewHolderItem);
            } else {
                viewHolderItem = (ViewHolderItem) convertView.getTag();
            }

            bind(viewHolderItem, note);

            return convertView;
        }

        public void filter(String text) {
            text = text.toLowerCase();
            List<Note> searchResult = new ArrayList<>();
            if (text.length() != 0) {
                for (Note note : mNotesList) {
                    if (note.getmTitle().contains(text)) {
                        searchResult.add(note);
                    }
                    setList(searchResult);
                }
            } else {
                setList(mNotesList);
            }
            notifyDataSetChanged();
        }

    }

//    class NoteCursorAdapter extends CursorAdapter {
//
//        private List<Note> mItems;
//        LayoutInflater mLayoutInflater;
//
//        NoteCursorAdapter(Context context, Cursor cursor, List<Note> items) {
//            super(context, cursor, false);
//            mItems = items;
//            mLayoutInflater = LayoutInflater.from(context);
//        }
//        @Override
//        public View newView(Context context, Cursor cursor, ViewGroup parent) {
//            return mLayoutInflater.inflate(R.layout.item_note_list, parent, false);
//
//        }
//
//        @Override
//        public void bindView(View view, Context context, Cursor cursor) {
//            ViewHolderItem viewHolderItem = new ViewHolderItem(view);
//        }
//    }
}

