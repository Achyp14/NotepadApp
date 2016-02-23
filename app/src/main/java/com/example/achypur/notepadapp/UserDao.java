package com.example.achypur.notepadapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLData;
import java.sql.SQLException;

public class UserDao {
    private SQLiteDatabase sqLiteDatabase;
    private DataBaseHelper dataBaseHelper;
    private String[] columns = {DataBaseHelper.KEY_ID,
            DataBaseHelper.KEY_LOGIN, DataBaseHelper.KEY_LOGGED};

    public UserDao(Context context) {
        dataBaseHelper = new DataBaseHelper(context);
    }

    public void open() throws SQLException {
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
    }

    public User createUser(String login, boolean logged) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_LOGIN, login);
        contentValues.put(DataBaseHelper.KEY_LOGGED, logged);

        long id = sqLiteDatabase.insert(DataBaseHelper.TABLE_USER, null, contentValues);

        Cursor cursor = sqLiteDatabase.query(DataBaseHelper.TABLE_USER, columns,
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
        user.setLogged(cursor.getInt(3) > 0);
        return user;
    }

}
