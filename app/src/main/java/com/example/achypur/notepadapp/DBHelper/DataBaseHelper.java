package com.example.achypur.notepadapp.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String LOG = "DataBaseHelper";
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "notepad.db";

    /*
        Common column
     */
    public static final String KEY_ID = "id";

    /*
        Users Table - column names
     */
    public static final String TABLE_USER = "users";
    public static final String KEY_LOGIN = "login";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_ROLE = "role";
    public static final String KEY_IMAGE = "image";

    /*
        Notes table - column names
     */
    public static final String TABLE_NOTE = "notes";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_USER = "user_id";
    public static final String KEY_CREATED_DATE = "date_of_creation";
    public static final String KEY_MODIFIED_DATE = "date_of_last_modified";
    public static final String KEY_POLICY_STATUS = "policy";

    /*
        Roles table - column names
     */
    public static final String TABLE_ROLE = "roles";
    public static final String KEY_ROLE = "role";

    /*
        Tag table - column names
     */
    public static final String TABLE_TAG = "tags";
    public static final String KEY_TAG = "tag";

    /*
        Tag of notes - column names
     */
    public static final String TABLE_TAG_NOTES = "tag_of_notes";
    public static final String KEY_NOTE_ID = "note_id";
    public static final String KEY_TAG_ID = "tag_id";
    public static final String KEY_USER_ID = "user_id";


    private static final String CREATE_TABLE_USER = "create table "
            + TABLE_USER + "(" + KEY_ID + " integer primary key autoincrement," + KEY_LOGIN
            + " text not null," + KEY_NAME + " TEXT not null," + KEY_EMAIL + " text not null," +
            KEY_PASSWORD + " text not null," + KEY_USER_ROLE + " text,"
            + KEY_IMAGE + " BLOB" + " )";

    private static final String CREATE_TABLE_NOTE = "create table " + TABLE_NOTE + "(" +
            KEY_ID + " integer primary key autoincrement," + KEY_TITLE + " text not null," +
            KEY_CONTENT + " text not null," + KEY_USER + " integer not null," +
            KEY_CREATED_DATE + " datetime," + KEY_MODIFIED_DATE + " datetime," +
            KEY_POLICY_STATUS + " integer" + ")";

    private static final String CREATE_TABLE_ROLE = "create table " + TABLE_ROLE + "(" +
            KEY_ID + " integer primary key autoincrement," + KEY_ROLE + " text not null" + ")";

    private static final String CREATE_TABLE_TAG = "create table " + TABLE_TAG + "(" +
            KEY_ID + " integer primary key autoincrement," + KEY_TAG + " text not null" + ")";

    public static final String CREATE_TAF_OF_NOTES = "create table " + TABLE_TAG_NOTES +
            "(" + KEY_ID + " integer primary key autoincrement," + KEY_NOTE_ID +
            " integer not null," + KEY_TAG_ID + " integer not null," +
            KEY_USER_ID + " integer not null" + ")";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_NOTE);
        db.execSQL(CREATE_TABLE_ROLE);
        db.execSQL(CREATE_TABLE_TAG);
        db.execSQL(CREATE_TAF_OF_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG_NOTES);
        onCreate(db);
    }
}
