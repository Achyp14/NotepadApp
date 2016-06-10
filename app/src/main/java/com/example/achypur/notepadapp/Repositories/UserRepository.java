package com.example.achypur.notepadapp.repositories;

import com.example.achypur.notepadapp.entities.User;

import java.util.List;

/**
 * Created by achypur on 26.05.2016.
 */
public interface UserRepository {
    User findUserById(Long id);

    Long finUserByLogin(String login);

    User createUser(User user);

    boolean isEmptyTable();

    List<User> findAll();

    void updateUser(Long id, String login, String name, String email, String password, byte[] image);

    void close();
}
