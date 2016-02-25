package com.example.achypur.notepadapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.achypur.notepadapp.DBHelper.DataBaseHelper;
import com.example.achypur.notepadapp.Entities.Note;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoteDao {
    private SQLiteDatabase mSqLiteDatabase;
    private DataBaseHelper mDataBaseHelper;
    private String[] mColumns = {DataBaseHelper.KEY_ID, DataBaseHelper.KEY_TITLE,
            DataBaseHelper.KEY_CONTENT, DataBaseHelper.KEY_CREATED_DATE,
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

    public Note createNote(String title, String content, String createdDate, String modifiedDate,
                           Long policyId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_TITLE, title);
        contentValues.put(DataBaseHelper.KEY_CONTENT, content);
        contentValues.put(DataBaseHelper.KEY_CREATED_DATE, createdDate);
        contentValues.put(DataBaseHelper.KEY_MODIFIED_DATE, modifiedDate);
        contentValues.put(DataBaseHelper.KEY_POLICY_STATUS, policyId);

        Long id = mSqLiteDatabase.insert(DataBaseHelper.TABLE_NOTE, null, contentValues);
        Cursor cursor = mSqLiteDatabase.query(DataBaseHelper.TABLE_NOTE, mColumns,
                DataBaseHelper.KEY_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        Note newNote = cursorToNote(cursor);
        cursor.close();
        return newNote;
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setmId(cursor.getLong(0));
        note.setmTitle(cursor.getString(1));
        note.setmContent(cursor.getString(2));
        note.setmCreatedDate(cursor.getString(3));
        note.setmModifiedDate(cursor.getString(4));
        note.setmPolicyId(cursor.getInt(5) == 0);
        return note;
    }

    public Note updateNote(Note note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_TITLE, note.getmTitle());
        contentValues.put(DataBaseHelper.KEY_CONTENT, note.getmContent());
        contentValues.put(mDataBaseHelper.KEY_CREATED_DATE, note.getmCreatedDate());
        contentValues.put(mDataBaseHelper.KEY_MODIFIED_DATE, note.getmModifiedDate());
        contentValues.put(mDataBaseHelper.KEY_POLICY_STATUS, note.getmPolicyStatus());

        long id = mSqLiteDatabase.update(mDataBaseHelper.TABLE_NOTE, contentValues,
                " id=" + note.getmId(), null);

        Cursor cursor = mSqLiteDatabase.query(DataBaseHelper.TABLE_NOTE, mColumns,
                DataBaseHelper.KEY_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        note = cursorToNote(cursor);
        cursor.close();
        return note;

    }

    public void deleteNote(Note note) {
        long id = note.getmId();
        mSqLiteDatabase.delete(mDataBaseHelper.TABLE_NOTE, mDataBaseHelper.KEY_ID + " = " + id, null);
    }


    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<Note>();

        Cursor cursor = mSqLiteDatabase.query(mDataBaseHelper.TABLE_NOTE, mColumns,
                null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Note note = cursorToNote(cursor);
            noteList.add(note);
            cursor.moveToNext();
        }
        cursor.close();
        return noteList;
    }


}
