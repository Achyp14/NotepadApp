package com.example.achypur.notepadapp.Repositories;


import com.example.achypur.notepadapp.Entities.Coordinate;
import com.example.achypur.notepadapp.Entities.ForecastEntity;
import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.Entities.Picture;
import com.example.achypur.notepadapp.Entities.Tag;
import com.example.achypur.notepadapp.Entities.TagOfNotes;
import com.example.achypur.notepadapp.JsonObjects.Forecast;

import java.util.List;

public interface NoteRepository {
    List<Note> findNotesByUserId(Long userId, int status);

    Note findNote(Long id);

    void updateNote(Note note);

    void deleteNote(Long id);

    Note createNote(String title, String content, Long userId, String createdDate, String modifiedDate, boolean policy, Long location);

    List<Tag> showAllTag();

    TagOfNotes createTagOfNotes(Long noteId, Long tagId, Long userId);

    List<Long> findTagOfNoteIds(Long noteId);

    void deleteTagOfNotes(Long noteId, Long tagId);

    Tag findTagByValue(String tag);

    List<Tag> findTagsById(List<Long> idList);

    Tag createTag(String content);

    Picture createPicture(byte[] image, Long noteId);

    List<byte[]> findAllPictureForCurrentNote(Long noteId);

    Long findPictureByNoteId(Long noteId);

    Picture findPicture(Long id);

    void deletePictureById(Long id);

    List<Long> findAllPicturesIdForCurrentNote(Long noteId);

    Long createCoordinateInDb(double latitude, double longtitude);

    Coordinate findCoordinateById(Long id);

    void deleteCoordinate(Long id);

    ForecastEntity createForecast(Forecast forecast, Long noteId);

    boolean isExistForecastForNote(Long noteId);

    Forecast findForecast(Long noteId);

    Forecast updateForecast(Forecast forecast, Long noteId);

    void deleteForecast(Long noteId);

}
