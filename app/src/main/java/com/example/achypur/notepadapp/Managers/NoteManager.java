package com.example.achypur.notepadapp.Managers;

import android.content.Context;

import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.Repositories.NoteRepository;
import com.example.achypur.notepadapp.RepositoriesImpl.NoteRepositoryImpl;

import java.util.List;

public class NoteManager {

    private NoteRepository mNoteRepository;
    Context mContext;
    Note mNote;

    public NoteManager(Context context){
        mContext = context;
    }


    public void createNoteRepo() {
        mNoteRepository = new NoteRepositoryImpl(mContext);
    }

    public List<Note> findAll(Long id, int status) {
        return mNoteRepository.findNotesByUserId(id, status);
    }

    public Note findNote(Long id) {
        return  mNoteRepository.findNote(id);
    }

    public void updateNote(Note note) {
        mNoteRepository.updateNote(note);
    }

    public void deleteNote(Long id) {
        mNoteRepository.deleteNote(id);
    }

    public Note createNote(String title, String content, Long userId, String createdDate, String modifiedDate, boolean policy, Long location) {
        mNote = mNoteRepository.createNote(title, content, userId, createdDate, modifiedDate, policy, location);
        return mNote;
    }

}
