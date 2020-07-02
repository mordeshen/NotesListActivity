package com.e.noteslistactivity.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.e.noteslistactivity.R;
import com.e.noteslistactivity.models.Note;
import com.e.noteslistactivity.utils.Utility;

import java.util.ArrayList;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {

    private static final String TAG = "NotesRecyclerAdapter";

    private ArrayList<Note> mNotes;
    OnNoteListener mOnNoteListener;

    public NotesRecyclerAdapter(ArrayList<Note> notes, OnNoteListener onNoteListener) {
        mNotes = new ArrayList<>();
        this.mNotes = notes;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_note_list_item,viewGroup,false);
        return new ViewHolder(view,mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        try {
            String month = mNotes.get(i).getTimeStamp().substring(0,2);
            month = Utility.getMonthFromNumber(month);
            String year = mNotes.get(i).getTimeStamp().substring(3);
            String timestamp = month + " " + year;
            viewHolder.tvTimeStamp.setText(timestamp);

            viewHolder.tvNoteTitle.setText(mNotes.get(i).getTitle());
        }
        catch (NullPointerException e){
            Log.e(TAG, "onBindViewHolder: NullPointerException "+ e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvNoteTitle, tvTimeStamp;
        OnNoteListener mOnNoteListener;

        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            tvNoteTitle = itemView.findViewById(R.id.note_title);
            tvTimeStamp = itemView.findViewById(R.id.note_time_stamp);
            mOnNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }

}

