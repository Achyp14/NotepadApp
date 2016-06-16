package com.example.achypur.notepadapp.managers;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

import com.example.achypur.notepadapp.BitmapDecoder;
import com.example.achypur.notepadapp.entities.Coordinate;
import com.example.achypur.notepadapp.entities.Note;
import com.example.achypur.notepadapp.entities.Picture;
import com.example.achypur.notepadapp.entities.Tag;
import com.example.achypur.notepadapp.jsonobjects.Forecast;
import com.example.achypur.notepadapp.repositories.NoteRepository;
import com.example.achypur.notepadapp.repositoriesimpl.NoteRepositoryImpl;
import com.example.achypur.notepadapp.view.PictureConvertor;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NoteManager {

    private NoteRepository mNoteRepository;
    Context mContext;
    Note mNote;
    PictureConvertor mPictureConvertor;

    public NoteManager(Context context) {
        mContext = context;
        mPictureConvertor = PictureConvertor.getInstance();
    }


    public void createNoteRepo() {
        mNoteRepository = new NoteRepositoryImpl(mContext);
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

    public List<String> findAllTagValueForCurrentNote(Long noteId) {
        List<Long> idList = mNoteRepository.findTagOfNoteIds(noteId);
        List<Tag> tagList = mNoteRepository.findTagsById(idList);

        List<String> contentList = new ArrayList<>();

        for (Tag tag : tagList) {
            contentList.add(tag.getmTag());
        }
        return contentList;
    }

    public List<Tag> findAllTagForCurrentNote(Long noteId) {
        List<Long> idList = mNoteRepository.findTagOfNoteIds(noteId);
        return mNoteRepository.findTagsById(idList);
    }

    public void createTags(List<Tag> tagList, Long noteId, Long userId) {
        for (Tag tag : tagList) {
            Tag item = mNoteRepository.findTagByValue(tag.getmTag());
            if (item != null) {
                mNoteRepository.createTagOfNotes(noteId, item.getmId(), userId);
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

    public void createPicture(Bitmap image, Long noteId) {
        mNoteRepository.createPicture(image.hashCode(), noteId);
        saveToInternalStorage(image, directory());
    }

    public void deletePicture(List<Bitmap> images, Long noteId) {
        for (int i = 0; i < images.size(); i++) {
            List<Integer> hashList = mNoteRepository.findAllPictureForCurrentNote(noteId);
            List<Long> idList = mNoteRepository.findAllPicturesIdForCurrentNote(noteId);
            for (Integer item : hashList) {
                if (item.equals(images.get(i).hashCode())) {
                    int position = hashList.indexOf(item);
                    Long id = idList.get(position);
                    mNoteRepository.deletePictureById(id);
                }
            }
        }
    }

    public List<Bitmap> findAllPictureForCurrentNote(Long noteId) {
        List<Integer> bytes = mNoteRepository.findAllPictureForCurrentNote(noteId);
        List<Bitmap> bitmapList = new ArrayList<>();

        for (Integer image : bytes) {
            bitmapList.add(loadImageFromStorage(directory(), image.hashCode()));
        }

        return bitmapList;
    }

    public LatLng findCurrentPosition(Long noteLocation) {
        Coordinate coordinate = mNoteRepository.findCoordinateById(noteLocation);
        return new LatLng(coordinate.getLatitude(), coordinate.getLongtitude());
    }

    public Long createLocation(double latitude, double longtitude) {
        return mNoteRepository.createCoordinateInDb(latitude, longtitude);
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

    public Picture findPicture(Long noteId) {
        Long id = mNoteRepository.findPictureByNoteId(noteId);
        if (id != null) {
            return mNoteRepository.findPicture(id);
        } else {
            return null;
        }
    }

    public void closeNote() {
        mNoteRepository.noteClose();
    }

    public void closeForecast() {
        mNoteRepository.forecastClose();
    }

    public void closeCoordinate() {
        mNoteRepository.coordinateClose();
    }

    public void closePicture() {
        mNoteRepository.pictureClose();
    }

    public void closeTag() {
        mNoteRepository.tagClose();
    }

    public void closeTagOfNotes() {
        mNoteRepository.tagOfNotesClose();
    }

    private void saveToInternalStorage(Bitmap bitmapImage, String directory) {
        File fileName = new File(directory, bitmapImage.hashCode() + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private Bitmap loadImageFromStorage(String path, Integer hash) {
        try {
            File f = new File(path, hash + ".jpg");
            BitmapDecoder bitmapDecoder = new BitmapDecoder();
            return bitmapDecoder.execute(new FileInputStream(f)).get();

        } catch (InterruptedException | FileNotFoundException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String directory() {
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        return directory.getAbsolutePath();
    }

}
