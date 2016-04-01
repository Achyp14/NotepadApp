package com.example.achypur.notepadapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.achypur.notepadapp.DBHelper.DataBaseHelper;
import com.example.achypur.notepadapp.Entities.Tag;

import java.sql.SQLException;
import java.util.List;

public class TagDao {
    private SQLiteDatabase mSqLiteDatabase;
    private DataBaseHelper mDataBaseHelper;

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
        Tag newTag = new Tag(tag);
        Long id = mSqLiteDatabase.insert(DataBaseHelper.TABLE_TAG, null, getTagContentValues(newTag));
        newTag.setmId(id);
        return newTag;
    }
    public void insertListTag(List<Tag> list) {
        for (Tag tag : list) {
            mSqLiteDatabase.insert(DataBaseHelper.TABLE_TAG, null, getTagContentValues(tag));
        }
    }

    private ContentValues getTagContentValues(Tag tag) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_TAG, tag.getmTag());
        return contentValues;
    }

    private Tag cursorToTag(Cursor cursor) {
        Tag tag = new Tag();
        tag.setmId(cursor.getLong(0));
        tag.setmTag(cursor.getString(1));
        return tag;
    }

    public String getTagById(Long id) {
        Cursor cursor = mSqLiteDatabase.rawQuery("select tag from " + mDataBaseHelper.TABLE_TAG +
                " where id = ?", new String[] {String.valueOf(id)});
        return cursor.getString(1);
    }

}
