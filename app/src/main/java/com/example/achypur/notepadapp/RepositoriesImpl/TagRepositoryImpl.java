package com.example.achypur.notepadapp.RepositoriesImpl;

import android.content.Context;

import com.example.achypur.notepadapp.DAO.TagDao;
import com.example.achypur.notepadapp.Entities.Tag;
import com.example.achypur.notepadapp.Repositories.TagRepository;

import java.sql.SQLException;
import java.util.List;

public class TagRepositoryImpl implements TagRepository{

    TagDao tagDao;

    public TagRepositoryImpl(Context context) {
        tagDao = new TagDao(context);

        try {
            tagDao.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Tag> findAll() {
        return tagDao.findAllTag();
    }
}
