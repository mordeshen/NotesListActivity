package com.e.noteslistactivity.async;

import android.os.AsyncTask;

import com.e.noteslistactivity.models.Note;
import com.e.noteslistactivity.persistence.NoteDao;

public class DeleteAsyncTask extends AsyncTask <Note,Void,Void>{

    private NoteDao mNoteDao;

    public DeleteAsyncTask(NoteDao dao) {
        mNoteDao = dao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        mNoteDao.delete(notes);
        return null;
    }
}
