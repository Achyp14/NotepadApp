package com.example.achypur.notepadapp.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.achypur.notepadapp.DBHelper.DataBaseHelper;
import com.example.achypur.notepadapp.Entities.TagOfNotes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TagOfNotesDao {
    private SQLiteDatabase mSqLiteDatabase;
    private DataBaseHelper mDataBaseHelper;
    private String[] mColumns = {DataBaseHelper.KEY_ID,
            DataBaseHelper.KEY_NOTE_ID, DataBaseHelper.KEY_TAG_ID, DataBaseHelper.KEY_USER_ID};

    public TagOfNotesDao(Context context) {
        mDataBaseHelper = new DataBaseHelper(context);
    }

    public void open() throws SQLException {
        mSqLiteDatabase = mDataBaseHelper.getWritableDatabase();
    }

    public void close() {
        mDataBaseHelper.close();
    }

    public TagOfNotes createTagOfNotes(Long noteId, Long tagId, Long userdId) {

        TagOfNotes tagOfNotes = new TagOfNotes(noteId, tagId, userdId);

        Long id = mSqLiteDatabase.insert(DataBaseHelper.TABLE_TAG_NOTES, null, getTagOfNotesContentValues(tagOfNotes));

        tagOfNotes.setmId(id);

        return tagOfNotes;
    }

    private ContentValues getTagOfNotesContentValues(TagOfNotes tagOfNotes) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_NOTE_ID, tagOfNotes.getmNotesId());
        contentValues.put(DataBaseHelper.KEY_TAG_ID, tagOfNotes.getmTagId());
        contentValues.put(DataBaseHelper.KEY_USER_ID, tagOfNotes.getmUserId());
        return contentValues;
    }

    public List<Long> getTagsByNoteId(Long id) {
        List<Long> idList = new ArrayList<>();
        Cursor cursor = mSqLiteDatabase.rawQuery("Select tag_id from " + mDataBaseHelper.TABLE_TAG_NOTES + " where note_id = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                idList.add(cursor.getLong(0));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return idList;

    }
}
