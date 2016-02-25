package com.example.achypur.notepadapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.achypur.notepadapp.DBHelper.DataBaseHelper;

import java.sql.SQLException;

public class TafOfNotesDao {
    private SQLiteDatabase mSqLiteDatabase;
    private DataBaseHelper mDataBaseHelper;
    private String[] mColumns = {DataBaseHelper.KEY_ID,
            DataBaseHelper.KEY_NOTE_ID, DataBaseHelper.KEY_TAG_ID, DataBaseHelper.KEY_USER_ID};

    public TafOfNotesDao(Context context) {
        mDataBaseHelper = new DataBaseHelper(context);
    }

    public void open() throws SQLException {
        mSqLiteDatabase = mDataBaseHelper.getWritableDatabase();
    }

    public void close() {
        mDataBaseHelper.close();
    }

    public void createTagOfNotes(Long noteId, Long tagId, Long userdId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_NOTE_ID, noteId);
        contentValues.put(DataBaseHelper.KEY_TAG_ID, tagId);
        contentValues.put(DataBaseHelper.KEY_USER_ID, userdId);

        Long id = mSqLiteDatabase.insert(DataBaseHelper.TABLE_TAG_NOTES, null, contentValues);

        mSqLiteDatabase.query(DataBaseHelper.CREATE_TAF_OF_NOTES, mColumns,
                DataBaseHelper.KEY_ID + " = " + id, null, null, null, null, null);
    }




}
