package com.example.achypur.notepadapp.Repositories;

import com.example.achypur.notepadapp.Entities.Tag;

import java.util.List;

public interface TagRepository {

    List<Tag> findAll();
}
