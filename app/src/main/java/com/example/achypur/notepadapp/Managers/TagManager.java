package com.example.achypur.notepadapp.Managers;


import android.content.Context;

import com.example.achypur.notepadapp.Entities.Tag;
import com.example.achypur.notepadapp.Repositories.TagRepository;
import com.example.achypur.notepadapp.RepositoriesImpl.TagRepositoryImpl;

import java.util.List;

public class TagManager {

    private Context mContext;
    private TagRepository mTagRepository;

    public TagManager(Context context) {
        mContext = context;
    }

    public void createTagRepo() {
        mTagRepository = new TagRepositoryImpl(mContext);
    }

    public List<Tag> findAll() {
        return mTagRepository.findAll();
    }
}
