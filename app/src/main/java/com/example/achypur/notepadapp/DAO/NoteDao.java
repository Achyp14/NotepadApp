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
        contentValues.put(mDataBaseHelper.KEY_CREATED_DATE, note.getmCreatedDate());
        contentValues.put(mDataBaseHelper.KEY_MODIFIED_DATE, note.getmModifiedDate());
        contentValues.put(mDataBaseHelper.KEY_POLICY_STATUS, note.getmPolicyStatus());

        return contentValues;
    }

    public Note createNote(String title, String content, Long userId, String createdDate, String modifiedDate,
                           Boolean policyId) {
        Note note = new Note(title, content, userId, createdDate, modifiedDate, policyId);
        Long id = mSqLiteDatabase.insert(DataBaseHelper.TABLE_NOTE, null, getNoteContentValues(note));

        note.setmId(id);

        return note;
    }

    private Note cursorToNote(Cursor cursor) {
        return new Note(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getLong(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getInt(6) == 0
        );
    }

    public void updateNote(Note note) {
        long id = mSqLiteDatabase.update(mDataBaseHelper.TABLE_NOTE, getNoteContentValues(note),
                " id= " + note.getmId(), null);
    }

    public void deleteNote(long id) {
        mSqLiteDatabase.delete(mDataBaseHelper.TABLE_NOTE, mDataBaseHelper.KEY_ID + " = " + id, null);
    }

    public void deleteNote(Note note) {
        deleteNote(note.getmId());
    }

    public Note getNoteById(Long id) {
        Cursor cursor = mSqLiteDatabase.rawQuery("Select * from " + mDataBaseHelper.TABLE_NOTE + " where id = ? ", new String[] {String.valueOf(id)});
        cursor.moveToFirst();
        return  cursorToNote(cursor);
    }

    public List<Note> getAllNotes() {
        Cursor cursor = mSqLiteDatabase.rawQuery("Select * from " + mDataBaseHelper.TABLE_NOTE, null);
        return addNoteToList(cursor);
    }


    public List<Note> getNotesByUserId(Long id) {
        Cursor cursor = mSqLiteDatabase.rawQuery("Select * from " + mDataBaseHelper.TABLE_NOTE + " where user_id = ?", new String[]{id.toString()});
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
