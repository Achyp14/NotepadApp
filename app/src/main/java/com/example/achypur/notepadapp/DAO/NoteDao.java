package com.example.achypur.notepadapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.achypur.notepadapp.DBHelper.DataBaseHelper;
import com.example.achypur.notepadapp.Entities.Note;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoteDao {
    private SQLiteDatabase mSqLiteDatabase;
    private DataBaseHelper mDataBaseHelper;
    private String[] mColumns = {DataBaseHelper.KEY_ID, DataBaseHelper.KEY_TITLE,
            DataBaseHelper.KEY_CONTENT, DataBaseHelper.KEY_USER, DataBaseHelper.KEY_CREATED_DATE,
            DataBaseHelper.KEY_MODIFIED_DATE, DataBaseHelper.KEY_POLICY_STATUS};

    public NoteDao(Context context) {
        mDataBaseHelper = new DataBaseHelper(context);
    }

    public void open() throws SQLException {
        mSqLiteDatabase = mDataBaseHelper.getWritableDatabase();
    }

    public void close() {
        mDataBaseHelper.close();
    }

    private ContentValues getNoteContentValues(Note note) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_TITLE, note.getmTitle());
        contentValues.put(DataBaseHelper.KEY_CONTENT, note.getmContent());
        contentValues.put(DataBaseHelper.KEY_USER, note.getmUserId());
        contentValues.put(DataBaseHelper.KEY_CREATED_DATE, note.getmCreatedDate());
        contentValues.put(DataBaseHelper.KEY_MODIFIED_DATE, note.getmModifiedDate());
        contentValues.put(DataBaseHelper.KEY_POLICY_STATUS, note.getmPolicyStatus());
        contentValues.put(DataBaseHelper.KEY_LOCAION, note.getmLocation());

        return contentValues;
    }

    public Note createNote(String title, String content, Long userId, String createdDate, String modifiedDate,
                           Boolean policyId, Long location) {
        Note note = new Note(title, content, userId, createdDate, modifiedDate, policyId, location);
        Long id = mSqLiteDatabase.insert(DataBaseHelper.TABLE_NOTE, null, getNoteContentValues(note));

        note.setmId(id);

        return note;
    }

    public void deleteAllNotes() {
        List<Note> list = getAllNotes();

        for (Note note : list) {
            mSqLiteDatabase.delete(DataBaseHelper.TABLE_NOTE, DataBaseHelper.KEY_ID + " = " + note.getmId(), null);
        }

    }


    private Note cursorToNote(Cursor cursor) {
        return new Note(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getLong(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getInt(6) > 0,
                cursor.getLong(7)
        );
    }

    public void updateNote(Note note) {
        mSqLiteDatabase.update(DataBaseHelper.TABLE_NOTE, getNoteContentValues(note),
                " id= " + note.getmId(), null);
    }

    public void deleteNote(long id) {
        mSqLiteDatabase.delete(DataBaseHelper.TABLE_NOTE, DataBaseHelper.KEY_ID + " = " + id, null);
    }


    public Note getNoteById(Long id) {
        if (id < 0)
            return null;
        for(Note n : getAllNotes()) {
            Log.e("Achyp", "95|NoteDao::getNoteById: " + n.getmTitle());
        }
        Cursor cursor = mSqLiteDatabase.rawQuery("Select * from " + DataBaseHelper.TABLE_NOTE + " where id = ? ", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        return cursorToNote(cursor);
    }

    public List<Note> getAllNotes() {
        Cursor cursor = mSqLiteDatabase.rawQuery("Select * from " + DataBaseHelper.TABLE_NOTE, null);
        return addNoteToList(cursor);
    }

    public List<Note> getNotesByUserId(Long id, int status) {
        Cursor cursor = mSqLiteDatabase.rawQuery("Select * from " + DataBaseHelper.TABLE_NOTE + " where user_id = ? or policy = ? ", new String[]{id.toString(), String.valueOf(status)});
        return addNoteToList(cursor);
    }


    public List<Note> addNoteToList(Cursor cursor) {
        List<Note> noteList = new ArrayList<Note>();
        Note note;
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                note = cursorToNote(cursor);
                noteList.add(note);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return noteList;
    }
}
