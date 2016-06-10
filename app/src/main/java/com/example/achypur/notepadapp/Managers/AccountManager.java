package com.example.achypur.notepadapp.managers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.achypur.notepadapp.entities.User;
import com.example.achypur.notepadapp.repositories.UserRepository;
import com.example.achypur.notepadapp.repositoriesimpl.UserRepositoryImpl;
import com.example.achypur.notepadapp.ui.LoginActivity;

import java.util.List;

public class AccountManager {

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Pref";

    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_PASSWORD = "IsPassword";


    public static final String KEY_LOGIN = "login";
    public static final String KEY_PASSWORD = "password";


    private static AccountManager instance;
    private UserRepository mUserRepository;
    private Context mContext = null;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private User mCurrentUser;

    public AccountManager(Context context) {
        mContext = context;
    }

    public void initLoginSession() {
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();
    }

    public String retrieveLogin() {
       return  mPref.getString(KEY_LOGIN, null);
    }

    public void createSession(String login, String password) {
        mEditor.putBoolean(IS_LOGIN, true);
        mEditor.putBoolean(IS_PASSWORD, true);
        mEditor.putString(KEY_LOGIN, login);
        mEditor.putString(KEY_PASSWORD, password);
        mEditor.commit();
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

    public void createUserRepository() {
        mUserRepository = new UserRepositoryImpl(mContext);
    }

    public User createUser(User user) {
        mCurrentUser = mUserRepository.createUser(user);
        return mCurrentUser;
    }

    public boolean isEmptyTable() {
        return mUserRepository.isEmptyTable();
    }

    public User createAdmin() {
        mCurrentUser = new User("admin", "Andrii", "achyp14@gmail.com", "admin", null, null);
        return createUser(mCurrentUser);
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public boolean checkPassword(String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkUserInDb(User user, List<User> userList) {
        for (User human : userList) {
            if (human.getLogin().equals(user.getLogin())) {
                return true;
            }
        }
        return false;
    }

    public List<User> findAllUsers() {
        return mUserRepository.findAll();
    }

    public boolean checkLogin(String login, String password, List<User> userList) {
        for (User someBody : userList) {
            if (someBody.getLogin().equals(login) && someBody.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public User findUserById(Long id) {
        return mUserRepository.findUserById(id);
    }

    public Long findUserId(String login) {
        return mUserRepository.finUserByLogin(login);
    }

    public void setUser(User user) {
        mCurrentUser = user;
    }

    public void updateUserInfo(Long id, String login, String name, String email, String password, byte[] image) {
        mUserRepository.updateUser(id, login, name, email, password, image);
    }

}
