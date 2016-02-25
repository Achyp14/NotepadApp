package com.example.achypur.notepadapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.achypur.notepadapp.DBHelper.DataBaseHelper;
import com.example.achypur.notepadapp.Entities.Tag;

import java.sql.SQLException;

public class TagDao {
    private SQLiteDatabase mSqLiteDatabase;
    private DataBaseHelper mDataBaseHelper;
    private String[] mColumns = {DataBaseHelper.KEY_ID, DataBaseHelper.KEY_TAG};

    public TagDao(Context context) {
        mDataBaseHelper = new DataBaseHelper(context);
    }

    public void open() throws SQLException {
        mSqLiteDatabase = mDataBaseHelper.getWritableDatabase();
    }

    public void close() {
        mDataBaseHelper.close();
    }

    public Tag createTag(String tag) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_TAG, tag);

        Long id = mSqLiteDatabase.insert(DataBaseHelper.TABLE_TAG, null, contentValues);

        Cursor cursor = mSqLiteDatabase.query(DataBaseHelper.TABLE_TAG, mColumns,
                DataBaseHelper.KEY_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        Tag newTag = cursorToTag(cursor);
        cursor.close();
        return newTag;
    }

    private Tag cursorToTag(Cursor cursor) {
        Tag tag = new Tag();
        tag.setmId(cursor.getLong(0));
        tag.setmTag(cursor.getString(1));
        return tag;
    }

}
