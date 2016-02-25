package com.example.achypur.notepadapp.Session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.achypur.notepadapp.Activities.LoginActivity;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    Context mContext;

    public static final String TAG = "Session";

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Pref";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_LOGIN = "login";

    public static final String KEY_ID = "id";

    public SessionManager(Context context) {
        this.mContext = context;
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();
    }

    public void createLoginSession(String login) {
        mEditor.putBoolean(IS_LOGIN, true);
        mEditor.putString(KEY_LOGIN, login);
        mEditor.commit();
    }

    public boolean checkLogin(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(mContext, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
            return true;
        }
        return false;
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_LOGIN, mPref.getString(KEY_LOGIN, null));
        user.put(KEY_ID, String.valueOf(mPref.getLong(KEY_ID, 0)));
        return user;
    }

    public void logoutUser() {
        mEditor.clear();
        mEditor.commit();
        Intent i = new Intent(mContext, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }

    public boolean isLoggedIn() {
        return mPref.getBoolean(IS_LOGIN, false);
    }
}


