package com.example.achypur.notepadapp.Repositories;


import com.example.achypur.notepadapp.Entities.Note;

import java.util.List;

public interface NoteRepository {
    List<Note> findNotesByUserId(Long userId, int status);

    Note findNote(Long id);

    void updateNote(Note note);

    void deleteNote(Long id);

    Note createNote(String title, String content, Long userId, String createdDate, String modifiedDate, boolean policy, Long location);

}
