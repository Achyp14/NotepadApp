package com.example.achypur.notepadapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private SQLiteDatabase mSqLiteDatabase;
    private DataBaseHelper mDataBaseHelper;
    private String[] mColumns = {DataBaseHelper.KEY_ID,
            DataBaseHelper.KEY_LOGIN};


    public UserDao(Context context) {
        mDataBaseHelper = new DataBaseHelper(context);
    }

    public void open() throws SQLException {
        mSqLiteDatabase = mDataBaseHelper.getWritableDatabase();
    }

    public User createUser(String login) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_LOGIN, login);

        long id = mSqLiteDatabase.insert(DataBaseHelper.TABLE_USER, null, contentValues);

        Cursor cursor = mSqLiteDatabase.query(DataBaseHelper.TABLE_USER, mColumns,
                DataBaseHelper.KEY_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        User newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(0));
        user.setLogin(cursor.getString(1));
        return user;
    }

    public void deleteUser(User user) {
        long id = user.getId();
        mSqLiteDatabase.delete(mDataBaseHelper.TABLE_USER, mDataBaseHelper.KEY_ID + " = " + id, null);
    }


    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();

        Cursor cursor = mSqLiteDatabase.query(mDataBaseHelper.TABLE_USER, mColumns,
                null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            User user = cursorToUser(cursor);
            userList.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        return userList;
    }




}
