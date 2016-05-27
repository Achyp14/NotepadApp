package com.example.achypur.notepadapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.achypur.notepadapp.DBHelper.DataBaseHelper;
import com.example.achypur.notepadapp.Entities.Tag;

import java.sql.SQLException;
import java.util.ArrayList;
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
        Long id = mSqLiteDatabase.insert(DataBaseHelper.TABLE_TAG, null, getTagContentValues(newTag.getmTag()));
        newTag.setmId(id);
        return newTag;
    }

    public List<Long> insertListTag(List<String> list) {
        List<Long> idList = new ArrayList<>();
        for (String tag : list) {
            idList.add(mSqLiteDatabase.insert(DataBaseHelper.TABLE_TAG, null, getTagContentValues(tag)));
        }
        return idList;
    }

    public void deleteListTag(List<Tag> list) {
        for (Tag tag : list) {
            mSqLiteDatabase.delete(DataBaseHelper.TABLE_TAG, " where id = ?",
                    new String[]{String.valueOf(tag.getmId())});
        }
    }

    private ContentValues getTagContentValues(String tag) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_TAG, tag);
        return contentValues;
    }

    private Tag cursorToTag(Cursor cursor) {
        return new Tag(cursor.getLong(0), cursor.getString(1));
    }

    public List<Tag> findTagsById(List<Long> idList) {
        List<Cursor> cursors = new ArrayList<>();
        for (Long id : idList) {
            cursors.add(mSqLiteDatabase.rawQuery("Select * from " +
                    mDataBaseHelper.TABLE_TAG + " where id = ? ", new String[]{String.valueOf(id)}));
        }
        return addTagToCursor(cursors);
    }

    private List<Tag> addTagToCursor(List<Cursor> cursorList) {
        List<Tag> tagList = new ArrayList<>();
        for (Cursor cursor : cursorList) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    tagList.add(cursorToTag(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return tagList;
    }


    public Tag findTagByValue(String tag) {
        Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + mDataBaseHelper.TABLE_TAG +
                        " where tag = ? ", new String[] {tag});
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            return cursorToTag(cursor);
        } else {
            return null;
        }
    }

    public List<Tag> findAllTag() {
        List<Cursor> cursor = new ArrayList<>();
        cursor.add(mSqLiteDatabase.rawQuery("select * from " + mDataBaseHelper.TABLE_TAG, null));
        return addTagToCursor(cursor);
    }

    public void deleteAll() {
        mSqLiteDatabase.delete(mDataBaseHelper.TABLE_TAG, null, null);
    }

}
