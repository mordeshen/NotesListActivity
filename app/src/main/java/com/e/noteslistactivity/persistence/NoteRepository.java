package com.e.noteslistactivity.persistence;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.e.noteslistactivity.async.DeleteAsyncTask;
import com.e.noteslistactivity.async.InsertAsyncTask;
import com.e.noteslistactivity.async.UpdateAsyncTask;
import com.e.noteslistactivity.models.Note;

import java.util.List;

public class NoteRepository
{
    private NoteDatabase mNoteDatabase;

    public Repository(Context context){
        appDBK = AppDBK.getInstance(context);
    }

    public NoteRepository(Context context){
        mNoteDatabase = NoteDatabase.getInstance(context);
    }

    public void insertNoteTask(Note note){
        new InsertAsyncTask(mNoteDatabase.getNoteDao()).execute(note);
    }

    public void updateNote(Note note){
        new UpdateAsyncTask(mNoteDatabase.getNoteDao()).execute(note);

    }

    public LiveData<List<Note>> retriveNoteTask(){
        return mNoteDatabase.getNoteDao().getNotes();
    }

    public void deleteNote(Note note){
        new DeleteAsyncTask(mNoteDatabase.getNoteDao()).execute(note);
    }
}
