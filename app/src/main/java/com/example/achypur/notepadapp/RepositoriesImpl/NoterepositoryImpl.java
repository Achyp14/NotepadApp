package com.example.achypur.notepadapp.RepositoriesImpl;


import android.content.Context;

import com.example.achypur.notepadapp.DAO.NoteDao;
import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.Repositories.NoteRepository;

import java.sql.SQLException;
import java.util.List;

public class NoterepositoryImpl implements NoteRepository {

    private NoteDao noteDao;

    public NoterepositoryImpl(Context context) {
        noteDao = new NoteDao(context);
        try {
           noteDao.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Note> findNotesByUserId(Long userId, int status) {
        return noteDao.getNotesByUserId(userId, status);
    }

    @Override
    public Note findNote(Long id) {
        return noteDao.getNoteById(id);
    }

    @Override
    public void updateNote(Note note) {
        noteDao.updateNote(note);
    }

    @Override
    public void deleteNote(Long id) {
        noteDao.deleteNote(id);
    }

    @Override
    public Note createNote(String title, String content, Long userId, String createdDate, String modifiedDate, boolean policy, Long location) {
       return noteDao.createNote(title, content, userId, createdDate, modifiedDate, policy, location);
    }



}
