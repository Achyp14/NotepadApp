package com.example.achypur.notepadapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.achypur.notepadapp.dbhelper.DataBaseHelper;

import java.sql.SQLException;

public class RoleDao {
    private SQLiteDatabase mSqLiteDatabase;
    private DataBaseHelper mDataBaseHelper;
    private String[] mColumns = {DataBaseHelper.KEY_ID, DataBaseHelper.KEY_ROLE};

    public RoleDao(Context context) {
        mDataBaseHelper = new DataBaseHelper(context);
    }

    public void open() throws SQLException {
        mSqLiteDatabase = mDataBaseHelper.getWritableDatabase();
    }

    public void close() {
        mDataBaseHelper.close();
    }

    public void CreateRole() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_ROLE, "ADMIN");
        contentValues.put(DataBaseHelper.KEY_ROLE, "USER");
        mSqLiteDatabase.insert(DataBaseHelper.TABLE_ROLE, null, contentValues);
    }
}
