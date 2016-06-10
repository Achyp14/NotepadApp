package com.example.achypur.notepadapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.achypur.notepadapp.dbhelper.DataBaseHelper;
import com.example.achypur.notepadapp.entities.TagofNotes;

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

    public TagofNotes createTagOfNotes(Long noteId, Long tagId, Long userdId) {

        TagofNotes tagofNotes = new TagofNotes(noteId, tagId, userdId);

        Long id = mSqLiteDatabase.insert(DataBaseHelper.TABLE_TAG_NOTES, null, getTagOfNotesContentValues(tagofNotes));

        tagofNotes.setmId(id);

        return tagofNotes;
    }

    private ContentValues getTagOfNotesContentValues(TagofNotes tagofNotes) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_NOTE_ID, tagofNotes.getmNotesId());
        contentValues.put(DataBaseHelper.KEY_TAG_ID, tagofNotes.getmTagId());
        contentValues.put(DataBaseHelper.KEY_USER_ID, tagofNotes.getmUserId());
        return contentValues;
    }

    public List<Long> findTagsId(Long noteId) {
        Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + mDataBaseHelper.TABLE_TAG_NOTES +
                " where note_id = ?", new String[]{String.valueOf(noteId)});
        return addTagOfNotesToCursor(cursor);
    }

    private List<Long> addTagOfNotesToCursor(Cursor cursor) {
        List<Long> idList = new ArrayList<>();
        TagofNotes tagofNotes;

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                tagofNotes = cursorToTagOfNotes(cursor);
                idList.add(tagofNotes.getmTagId()); // Get tag id
                cursor.moveToNext();
            }
        }
        cursor.close();
        return idList;
    }

    private TagofNotes cursorToTagOfNotes(Cursor cursor) {
        return new TagofNotes(
                cursor.getLong(0),
                cursor.getLong(1),
                cursor.getLong(2),
                cursor.getLong(3)
        );
    }

    public void deleteTag(Long noteId, Long tagId) {
        mSqLiteDatabase.delete(mDataBaseHelper.TABLE_TAG_NOTES
                , " note_id = ? and tag_id = ?", new String[]{String.valueOf(noteId), String.valueOf(tagId)});
    }

    public void deleteAll() {
        mSqLiteDatabase.delete(mDataBaseHelper.TABLE_TAG_NOTES, null, null);
    }
}
