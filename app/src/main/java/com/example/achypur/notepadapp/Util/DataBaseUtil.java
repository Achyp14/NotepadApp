package com.example.achypur.notepadapp.Util;

import android.util.Log;

import com.example.achypur.notepadapp.DAO.TagDao;
import com.example.achypur.notepadapp.DAO.TagOfNotesDao;
import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.Entities.Tag;
import com.example.achypur.tagview.TagView;

import java.util.ArrayList;
import java.util.List;

public class DataBaseUtil {

    TagOfNotesDao mTagOfNotesDao;
    TagView mTagView;
    TagDao mTagDao;
    Note mNote;

    public DataBaseUtil(TagOfNotesDao tagOfNotesDao, TagView tagView, TagDao tagDao, Note note) {
        mTagOfNotesDao = tagOfNotesDao;
        mTagView = tagView;
        mTagDao = tagDao;
        mNote = note;
    }

    public void setmNote(Note mNote) {
        this.mNote = mNote;
    }

    public List<Tag> showAllTags(Long noteId, List<Tag> list) {
        List<Long> idList = mTagOfNotesDao.findTagsId(noteId);
        list = mTagDao.findTagsById(idList);
        List<String> tagList = new ArrayList<>();
        for (Tag tag : list) {
            tagList.add(tag.getmTag());
        }
        mTagView.setList(tagList);
        return list;
    }

    public List<Tag> getAllTag() {
        return mTagDao.findAllTag();
    }

    public void createTagInDb(List<Tag> list) {
        for (Tag tag : list) {
            Tag item = mTagDao.findTagByValue(tag.getmTag());
            if (item != null) {
                mTagOfNotesDao.createTagOfNotes(mNote.getmId(), item.getmId(), mNote.getmUserId());
            } else {
                Log.e("Achyp", "49|DataBaseUtil::createTagInDb: "  + mNote.getmId());
                item = mTagDao.createTag(tag.getmTag());
                mTagOfNotesDao.createTagOfNotes(mNote.getmId(), item.getmId(), mNote.getmUserId());
            }
        }
    }

    public void deleteTagFromDb(List<Tag> list) {
        for (Tag itemList : list) {
            Tag tag = mTagDao.findTagByValue(itemList.getmTag());
            mTagOfNotesDao.deleteTag(mNote.getmId(), tag.getmId());
        }
    }

}
