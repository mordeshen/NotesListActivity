package com.e.noteslistactivity.async;

import android.os.AsyncTask;

import com.e.noteslistactivity.models.Note;
import com.e.noteslistactivity.persistence.NoteDao;

public class InsertAsyncTask extends AsyncTask <Note,Void,Void>{

    private NoteDao mNoteDao;

    public InsertAsyncTask(NoteDao dao) {
        mNoteDao = dao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        mNoteDao.insertNotes(notes);
        return null;
    }
}
