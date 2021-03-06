package com.example.achypur.notepadapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.achypur.notepadapp.entities.User;
import com.example.achypur.notepadapp.dbhelper.DataBaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private SQLiteDatabase mSqLiteDatabase;
    private DataBaseHelper mDataBaseHelper;
    private String[] mColumns = {DataBaseHelper.KEY_ID,
            DataBaseHelper.KEY_LOGIN, DataBaseHelper.KEY_NAME, DataBaseHelper.KEY_EMAIL,
            DataBaseHelper.KEY_PASSWORD, DataBaseHelper.KEY_USER_ROLE, DataBaseHelper.KEY_IMAGE};

    public UserDao(Context context) {
        mDataBaseHelper = new DataBaseHelper(context);
    }

    public void open() throws SQLException {
        mSqLiteDatabase = mDataBaseHelper.getWritableDatabase();
    }

    public void close() {
        mDataBaseHelper.close();
    }

    public User createUser(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_LOGIN, user.getLogin());
        contentValues.put(DataBaseHelper.KEY_NAME, user.getName());
        contentValues.put(DataBaseHelper.KEY_EMAIL, user.getEmail());
        contentValues.put(DataBaseHelper.KEY_PASSWORD, user.getPassword());
        contentValues.put(DataBaseHelper.KEY_USER_ROLE, user.getRole());
        contentValues.put(DataBaseHelper.KEY_IMAGE, user.getImage());
        Long id = mSqLiteDatabase.insert(DataBaseHelper.TABLE_USER, null, contentValues);
        user.setId(id);
        return user;
    }

    public User createUser(String login, String name, String email, String password, String role, byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_LOGIN, login);
        contentValues.put(DataBaseHelper.KEY_NAME, name);
        contentValues.put(DataBaseHelper.KEY_EMAIL, email);
        contentValues.put(DataBaseHelper.KEY_PASSWORD, password);
        contentValues.put(DataBaseHelper.KEY_USER_ROLE, role);
        contentValues.put(DataBaseHelper.KEY_IMAGE, image);

        Long id = mSqLiteDatabase.insert(DataBaseHelper.TABLE_USER, null, contentValues);

        Cursor cursor = mSqLiteDatabase.query(DataBaseHelper.TABLE_USER, mColumns,
                DataBaseHelper.KEY_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        User newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
    }

    public void updateUser(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_LOGIN, user.getLogin());
        contentValues.put(DataBaseHelper.KEY_NAME, user.getName());
        contentValues.put(DataBaseHelper.KEY_EMAIL, user.getEmail());
        contentValues.put(DataBaseHelper.KEY_PASSWORD, user.getPassword());
        contentValues.put(DataBaseHelper.KEY_USER_ROLE, user.getRole());
        contentValues.put(DataBaseHelper.KEY_IMAGE, user.getImage());

        mSqLiteDatabase.update(DataBaseHelper.TABLE_USER, contentValues, " id= " + user.getId(), null);
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(0));
        user.setLogin(cursor.getString(1));
        user.setName(cursor.getString(2));
        user.setEmail(cursor.getString(3));
        user.setPassword(cursor.getString(4));
        user.setRole(cursor.getString(5));
        user.setImage(cursor.getBlob(6));
        return user;
    }

    public void deleteUser(User user) {
        long id = user.getId();
        mSqLiteDatabase.delete(DataBaseHelper.TABLE_USER, DataBaseHelper.KEY_ID + " = " + id, null);
    }


    public void deleteAll() {
        for (User user : getAllUsers()) {
            mSqLiteDatabase.delete(DataBaseHelper.TABLE_USER, DataBaseHelper.KEY_ID + " = " + user.getId(), null);
        }
    }


    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();

        Cursor cursor = mSqLiteDatabase.rawQuery("Select * from " + DataBaseHelper.TABLE_USER, null);
        User user;
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                user = cursorToUser(cursor);
                userList.add(user);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return userList;
    }

    public boolean isEmpty() {
        Cursor cursor = mSqLiteDatabase.rawQuery("Select * from " + mDataBaseHelper.TABLE_USER, null);
        if (cursor.moveToFirst()) {
            return false;
        } else {
            return true;
        }
    }


    public Long findUserByLogin(String login) {
        Cursor cursor = mSqLiteDatabase.rawQuery("Select * from " + mDataBaseHelper.TABLE_USER + " where login = ?", new String[]{login});
        cursor.moveToFirst();
        return cursor.getLong(0);
    }

    public User findUserById(Long id) {
        Cursor cursor = mSqLiteDatabase.rawQuery("Select * from " + mDataBaseHelper.TABLE_USER + " where id = ?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        return cursorToUser(cursor);
    }
}
