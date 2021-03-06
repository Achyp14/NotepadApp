package com.example.achypur.notepadapp.repositoriesimpl;


import android.content.Context;

import com.example.achypur.notepadapp.dao.CoordinateDao;
import com.example.achypur.notepadapp.dao.ForecastDao;
import com.example.achypur.notepadapp.dao.NoteDao;
import com.example.achypur.notepadapp.dao.PictureDao;
import com.example.achypur.notepadapp.dao.TagDao;
import com.example.achypur.notepadapp.dao.TagOfNotesDao;
import com.example.achypur.notepadapp.entities.Coordinate;
import com.example.achypur.notepadapp.entities.ForecastEntity;
import com.example.achypur.notepadapp.entities.Note;
import com.example.achypur.notepadapp.entities.Picture;
import com.example.achypur.notepadapp.entities.Tag;
import com.example.achypur.notepadapp.entities.TagofNotes;
import com.example.achypur.notepadapp.jsonobjects.Forecast;
import com.example.achypur.notepadapp.repositories.NoteRepository;

import java.sql.SQLException;
import java.util.List;

public class NoteRepositoryImpl implements NoteRepository {

    private NoteDao mNoteDao;
    private TagDao mTagDao;
    private TagOfNotesDao mTagOfNotesDao;
    private PictureDao mPictureDao;
    private CoordinateDao mCoordinateDao;
    private ForecastDao mForecastDao;

    public NoteRepositoryImpl(Context context) {
        mNoteDao = new NoteDao(context);
        mTagDao = new TagDao(context);
        mTagOfNotesDao = new TagOfNotesDao(context);
        mPictureDao = new PictureDao(context);
        mCoordinateDao = new CoordinateDao(context);
        mForecastDao = new ForecastDao(context);

        try {
            mNoteDao.open();
            mTagDao.open();
            mTagOfNotesDao.open();
            mPictureDao.open();
            mCoordinateDao.open();
            mForecastDao.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Note> findNotesByUserId(Long userId, int status) {
        return mNoteDao.getNotesByUserId(userId, status);
    }

    @Override
    public Note findNote(Long id) {
        return mNoteDao.getNoteById(id);
    }

    @Override
    public void updateNote(Note note) {
        mNoteDao.updateNote(note);
    }

    @Override
    public void deleteNote(Long id) {
        mNoteDao.deleteNote(id);
    }

    @Override
    public Note createNote(String title, String content, Long userId, String createdDate, String modifiedDate, boolean policy, Long location) {
        return mNoteDao.createNote(title, content, userId, createdDate, modifiedDate, policy, location);
    }

    @Override
    public List<Tag> showAllTag() {
        return mTagDao.findAllTag();
    }

    @Override
    public TagofNotes createTagOfNotes(Long noteId, Long tagId, Long userId) {
        return mTagOfNotesDao.createTagOfNotes(noteId, tagId, userId);
    }

    @Override
    public void deleteTagOfNotes(Long noteId, Long tagId) {
        mTagOfNotesDao.deleteTag(noteId, tagId);
    }

    @Override
    public List<Long> findTagOfNoteIds(Long noteId) {
        return mTagOfNotesDao.findTagsId(noteId);
    }

    @Override
    public Tag findTagByValue(String tag) {
        return mTagDao.findTagByValue(tag);
    }

    @Override
    public List<Tag> findTagsById(List<Long> idList) {
        return mTagDao.findTagsById(idList);
    }

    @Override
    public Tag createTag(String content) {
        return mTagDao.createTag(content);
    }

    @Override
    public Picture createPicture(Integer image, Long noteId) {
        return mPictureDao.createPicture(image, noteId);
    }

    @Override
    public List<Integer> findAllPictureForCurrentNote(Long noteId) {
        return mPictureDao.getAllPicture(noteId);
    }

    @Override
    public Long findPictureByNoteId(Long noteId) {
        return mPictureDao.findPictureByNoteId(noteId);
    }

    @Override
    public Picture findPicture(Long id) {
        return mPictureDao.findPictureById(id);
    }

    @Override
    public void deletePictureById(Long id) {
        mPictureDao.deletePictureById(id);
    }

    @Override
    public List<Long> findAllPicturesIdForCurrentNote(Long noteId) {
        return mPictureDao.getAllPictureId(noteId);
    }

    @Override
    public Long createCoordinateInDb(double latitude, double longtitude) {
        return mCoordinateDao.createCoordinate(latitude, longtitude);
    }

    @Override
    public Coordinate findCoordinateById(Long id) {
        return mCoordinateDao.getCoordinateById(id);
    }

    @Override
    public void deleteCoordinate(Long id) {
        mCoordinateDao.deleteCoordinate(id);
    }

    @Override
    public ForecastEntity createForecast(Forecast forecast, Long noteId) {
        return mForecastDao.createForecast(forecast, noteId);
    }

    @Override
    public boolean isExistForecastForNote(Long noteId) {
        return mForecastDao.ifExistForecastForNote(noteId);
    }

    @Override
    public Forecast findForecast(Long noteId) {
        return mForecastDao.forecastEntityToForecast(noteId);
    }

    @Override
    public Forecast updateForecast(Forecast forecast, Long noteId) {
        return mForecastDao.updateWeather(forecast, noteId);
    }

    @Override
    public void deleteForecast(Long noteId) {
        mForecastDao.deleteForecast(noteId);
    }

    @Override
    public void noteClose() {
        mNoteDao.close();
    }

    @Override
    public void pictureClose() {
        mPictureDao.close();
    }

    @Override
    public void tagClose() {
        mTagDao.close();
    }

    @Override
    public void forecastClose() {
        mForecastDao.close();
    }

    @Override
    public void coordinateClose() {
        mCoordinateDao.close();
    }

    @Override
    public void tagOfNotesClose() {
        mTagOfNotesDao.close();
    }
}
