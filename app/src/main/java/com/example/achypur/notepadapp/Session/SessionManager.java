package com.example.achypur.notepadapp.Session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;

import com.example.achypur.notepadapp.UI.LoginActivity;
import com.example.achypur.notepadapp.UI.MainActivity;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    Context mContext;

    public static final String TAG = "Session";

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Pref";

    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_PASSWORD = "IsPassword";


    public static final String KEY_LOGIN = "login";
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_ID = "id";

    public SessionManager(Context context) {
        this.mContext = context;
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();
    }

    public void createLoginSession(String login, String password) {
        mEditor.putBoolean(IS_LOGIN, true);
        mEditor.putBoolean(IS_PASSWORD, true);
        mEditor.putString(KEY_LOGIN, login);
        mEditor.putString(KEY_PASSWORD, password);
        mEditor.commit();
    }

    public boolean checkLogin() {
        Intent i;
        if (!isLoggedIn()) {
            i = new Intent(mContext, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
            return false;
        } else {
            return true;
        }
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_LOGIN, mPref.getString(KEY_LOGIN, null));
        user.put(KEY_PASSWORD, mPref.getString(KEY_PASSWORD, null));
        user.put(KEY_ID, String.valueOf(mPref.getLong(KEY_ID, 0)));
        return user;
    }

    public void logoutUser() {
        mEditor.putBoolean(IS_LOGIN, false);
        mEditor.putBoolean(IS_PASSWORD, false);
        mEditor.clear();
        mEditor.commit();
        Intent i = new Intent(mContext, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }


    public boolean isLoggedIn() {
        if (mPref.getBoolean(IS_LOGIN, false) && mPref.getBoolean(IS_PASSWORD, false)) {
            return mPref.getBoolean(IS_LOGIN, false);
        } else
            return (mPref.getBoolean(IS_LOGIN, false));
    }


}


