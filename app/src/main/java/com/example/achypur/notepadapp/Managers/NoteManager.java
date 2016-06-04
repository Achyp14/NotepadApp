package com.example.achypur.notepadapp.Managers;

import android.content.Context;

import com.example.achypur.notepadapp.Entities.Coordinate;
import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.Entities.Picture;
import com.example.achypur.notepadapp.Entities.Tag;
import com.example.achypur.notepadapp.JsonObjects.Forecast;
import com.example.achypur.notepadapp.Repositories.NoteRepository;
import com.example.achypur.notepadapp.RepositoriesImpl.NoterepositoryImpl;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoteManager {

    private NoteRepository mNoteRepository;
    Context mContext;
    Note mNote;

    public NoteManager(Context context) {
        mContext = context;
    }


    public void createNoteRepo() {
        mNoteRepository = new NoterepositoryImpl(mContext);
    }

    public List<Note> findAll(Long id, int status) {
        return mNoteRepository.findNotesByUserId(id, status);
    }

    public Note findNote(Long id) {
        return mNoteRepository.findNote(id);
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

    public List<String> findAllTagForCurrentNote(Long noteId) {
        List<Long> idList = mNoteRepository.findTagOfNoteIds(noteId);
        List<Tag> tagList = mNoteRepository.findTagsById(idList);

        List<String> contentList = new ArrayList<>();

        for (Tag tag : tagList) {
            contentList.add(tag.getmTag());
        }
        return contentList;
    }

    public void createTags(List<Tag> tagList, Long noteId, Long userId) {
        for (Tag tag : tagList) {
            Tag item = mNoteRepository.findTagByValue(tag.getmTag());
            if (item != null) {
                mNoteRepository.createTagOfNotes(noteId, tag.getmId(), userId);
            } else {
                item = mNoteRepository.createTag(tag.getmTag());
                mNoteRepository.createTagOfNotes(noteId, item.getmId(), userId);
            }

        }
    }

    public void deleteTags(List<Tag> tagList, Long noteId) {
        Tag item;
        for (Tag tag : tagList) {
            item = mNoteRepository.findTagByValue(tag.getmTag());
            mNoteRepository.deleteTagOfNotes(noteId, item.getmId());
        }
    }

    public List<String> showAllTag() {
        List<String> tags = new ArrayList<>();
        for (Tag tag : mNoteRepository.showAllTag()) {
            tags.add(tag.getmTag());
        }
        return tags;
    }

    public void createPicture(byte[] image, Long noteId) {
        mNoteRepository.createPicture(image, noteId);
    }

    public void deletePicture(List<byte[]> images, Long noteId) {
        for (int i = 0; i < images.size(); i++) {
            List<byte[]> pictureList = mNoteRepository.findAllPictureForCurrentNote(noteId);
            List<Long> idList = mNoteRepository.findAllPicturesIdForCurrentNote(noteId);
            for (byte[] item : pictureList) {
                if (Arrays.equals(item, images.get(i))) {
                    int position = pictureList.indexOf(item);
                    Long id = idList.get(position);
                    mNoteRepository.deletePictureById(id);
                }
            }
        }
    }

    public List<byte[]> findAllPictureForCurrentNote(Long noteId) {
        return mNoteRepository.findAllPictureForCurrentNote(noteId);
    }

    public LatLng findCurrentPosition(Long noteLocation) {
        Coordinate coordinate = mNoteRepository.findCoordinateById(noteLocation);
        return new LatLng(coordinate.getLatitude(), coordinate.getLongtitude());
    }

    public Long createLocation(double latitude, double longtitude) {
        return mNoteRepository.createCoordinateInDb(latitude, longtitude);
    }

    public void removeLocation(Long id) {
        mNoteRepository.deleteCoordinate(id);
    }

    public boolean ifExistForecast(Long noteId) {
        return mNoteRepository.isExistForecastForNote(noteId);
    }

    public void createForecast(Forecast forecast, Long noteId) {
        mNoteRepository.createForecast(forecast, noteId);
    }

    public Forecast findForecast(Long noteId) {
        return mNoteRepository.findForecast(noteId);
    }

    public void removeForecast(Long noteId) {
        mNoteRepository.deleteForecast(noteId);
    }

    public Forecast updateForecast(Forecast forecast, Long noteId) {
        return mNoteRepository.updateForecast(forecast, noteId);
    }

    public Picture findPicute(Long noteId) {
        Long id = mNoteRepository.findPictureByNoteId(noteId);
        if (id != null) {
            return  mNoteRepository.findPicture(id);
        } else {
            return null;
        }
    }
}
