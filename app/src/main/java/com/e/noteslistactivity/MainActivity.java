package com.e.noteslistactivity;

import android.content.Intent;
import androidx.annotation.NonNull;

import com.e.noteslistactivity.persistence.NoteRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;


import com.e.noteslistactivity.adapters.NotesRecyclerAdapter;
import com.e.noteslistactivity.models.Note;
import com.e.noteslistactivity.utils.VerticalSpacingItemDecorator;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NotesRecyclerAdapter.OnNoteListener, FloatingActionButton.OnClickListener {

    //UI component
    RecyclerView rvNotes;

    //vars
    ArrayList <Note> mNotes = new ArrayList<>();
    NotesRecyclerAdapter mNotesRecyclerAdapter;
    NoteRepository mNoteRepository;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvNotes = findViewById(R.id.rvNotes);
        findViewById(R.id.fab).setOnClickListener(this);

        mNoteRepository = new NoteRepository(this);



        initRV();
        retriveNotes();
//        insertFakeData();
//        mNoteRepository.insertNoteTask(new Note());

        setSupportActionBar((Toolbar)findViewById(R.id.notes_toolbar));
        setTitle("Notes");
    }

    private void retriveNotes() {
        mNoteRepository.retriveNoteTask().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if (mNotes.size()>0){
                    mNotes.clear();
                }
                if (notes!=null){
                    mNotes.addAll(notes);
                }
                mNotesRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void insertFakeData() {
        for (int i = 0; i < 1000; i++) {
            Note note = new Note();
            note.setTitle("title # "+ i);
            note.setContent("content # " +i);
            note.setTimeStamp("aug 2019");
            mNotes.add(note);
        }
        mNotesRecyclerAdapter.notifyDataSetChanged();
    }

    private void initRV() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvNotes.setLayoutManager(linearLayoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        rvNotes.addItemDecoration(itemDecorator);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvNotes);
        mNotesRecyclerAdapter = new NotesRecyclerAdapter(mNotes, this);
        rvNotes.setAdapter(mNotesRecyclerAdapter);
    }

    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(this,NoteActivity.class);
        intent.putExtra("selected_note",mNotes.get(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,NoteActivity.class);
        startActivity(intent);

    }

    private void deleteNote(Note note){
        mNotes.remove(note);
        mNotesRecyclerAdapter.notifyDataSetChanged();

        mNoteRepository.deleteNote(note);
    }

    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT){

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            deleteNote(mNotes.get(viewHolder.getAdapterPosition()));
        }
    };
}
