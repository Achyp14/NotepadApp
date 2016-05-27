package com.example.achypur.notepadapp.RepositoriesImpl;

import android.content.Context;

import com.example.achypur.notepadapp.DAO.UserDao;
import com.example.achypur.notepadapp.Entities.User;
import com.example.achypur.notepadapp.Repositories.UserRepository;

import java.sql.SQLException;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    UserDao mUserDao;

    public UserRepositoryImpl(Context context) {
        mUserDao = new UserDao(context);
        try {
            mUserDao.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User findUserById(Long id) {
        return mUserDao.findUserById(id);
    }

    @Override
    public Long finUserByLogin(String login) {
        return mUserDao.findUserByLogin(login);
    }

    @Override
    public User createUser(User user) {
        return mUserDao.createUser(user);
    }

    @Override
    public boolean isEmptyTable() {
        return mUserDao.isEmpty();
    }

    @Override
    public List<User> findAll() {
        return mUserDao.getAllUsers();
    }

    @Override
    public void updateUser(Long id, String name, String login, String email, String password, byte[] image) {
        User user = new User(id, name, login, email, password, null, image);
        mUserDao.updateUser(user);
    }
}
