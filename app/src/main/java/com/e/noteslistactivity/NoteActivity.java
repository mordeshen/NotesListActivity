package com.e.noteslistactivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e.noteslistactivity.models.Note;
import com.e.noteslistactivity.persistence.NoteRepository;
import com.e.noteslistactivity.utils.Utility;

public class NoteActivity extends AppCompatActivity
implements View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener, View.OnClickListener, TextWatcher
{


    private static final String TAG = "NoteActivity";
    private static final int EDIT_MODE_ENABLE = 1;
    private static final int EDIT_MODE_DISABLE = 0;


    //ui components
    private LineEditText mLineEditText;
    private EditText mEditTitle;
    private TextView mViewTitle;
    private RelativeLayout mCheckContainer, mBackArrowContainer;
    private ImageButton mCheck, mBackArrow;

    //vars
    private boolean mIsNewNote;
    private Note mInitialNote,mFinalNote;
    private GestureDetector mGestureDetector;
    private int mMode;
    private NoteRepository mNoteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        setPointer();
        initNoteVars();

        if (getIncomingNote()){
            //this is a new note, (EDIT MODE)
            setNewProperties();
            enableEditMode();
        }
        else {
            // this is not a new note (TEXT MODE)
            disableEditMode();
            setNoteProperties();

        }
        setListeners();
    }

    private void initNoteVars() {
        mNoteRepository = new NoteRepository(this);
    }

    private void setPointer() {
        mLineEditText = findViewById(R.id.note_text);
        mEditTitle =  findViewById(R.id.note_edit_title);
        mViewTitle = findViewById(R.id.note_text_title);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mCheck = findViewById(R.id.toolbar_check);
        mBackArrow =  findViewById(R.id.toolbar_back_arrow);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners(){
        mLineEditText.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(this,this);
        mViewTitle.setOnClickListener(this);
        mCheck.setOnClickListener(this);
        mBackArrow.setOnClickListener(this);
        mEditTitle.addTextChangedListener(this);
    }

    private boolean getIncomingNote(){
        if(getIntent().hasExtra("selected_note")){
            mInitialNote = getIntent().getParcelableExtra("selected_note");
//         mFinalNote = getIntent().getParcelableExtra("selected_note");

            mFinalNote = new Note();
            mFinalNote.setTitle(mInitialNote.getTitle());
            mFinalNote.setContent(mInitialNote.getContent());
            mFinalNote.setTimeStamp(mInitialNote.getTimeStamp());
            mFinalNote.setId(mInitialNote.getId());

            disableEditMode();
            mMode = EDIT_MODE_DISABLE;

            mIsNewNote = false;
            return false;
        }
        enableEditMode();
        mMode = EDIT_MODE_ENABLE;

        mIsNewNote = true;
        return true;
    }

    private void setNewProperties(){
        mViewTitle.setText("Note Title");
        mEditTitle.setText("Note Title");
        mInitialNote = new Note();
        mFinalNote = new Note();

        mInitialNote.setTitle("Note Title");
        mFinalNote.setTitle("Note Title");
    }
    private void setNoteProperties(){
        mViewTitle.setText(mInitialNote.getTitle());
        mEditTitle.setText(mInitialNote.getTitle());
        mLineEditText.setText(mInitialNote.getContent());
    }

    private void saveChanges(){
        if (mIsNewNote){

            saveNewNote();
        }
        else {
            //update
            updateNote();
        }
    }
    private void saveNewNote(){
        mNoteRepository.insertNoteTask(mFinalNote);
    }
    private void updateNote(){
        mNoteRepository.updateNote(mFinalNote);
    }

    private void enableEditMode(){
        mBackArrowContainer.setVisibility(View.GONE);
        mCheckContainer.setVisibility(View.VISIBLE);

        mViewTitle.setVisibility(View.GONE);
        mEditTitle.setVisibility(View.VISIBLE);

        mMode = EDIT_MODE_ENABLE;
        enableContentInteraction();
    }
    private void disableEditMode(){
        mCheckContainer.setVisibility(View.GONE);
        mBackArrowContainer.setVisibility(View.VISIBLE);

        mEditTitle.setVisibility(View.GONE);
        mViewTitle.setVisibility(View.VISIBLE);

        mMode = EDIT_MODE_DISABLE;
        disableContentInteraction();

        String temp = mLineEditText.getText().toString();
        temp.replace("\n","");
        temp.replace(" ", "");
        if (temp.length()>0){
            mFinalNote.setTitle(mEditTitle.getText().toString());
            mFinalNote.setContent(mLineEditText.getText().toString());
            String timeStamp = Utility.getCurrentTimeStamp();
            mFinalNote.setTimeStamp(timeStamp);

            if (!mFinalNote.getContent().equals(mInitialNote.getContent())|| !mFinalNote.getTitle().equals(mInitialNote.getTitle())){
                Log.d(TAG, "disableEditMode: called");
                saveChanges();
            }
        }
    }

    private void disableContentInteraction(){
        mLineEditText.setKeyListener(null);
        mLineEditText.setFocusable(false);
        mLineEditText.setFocusableInTouchMode(false);
        mLineEditText.setCursorVisible(false);
        mLineEditText.clearFocus();
    }
    private void enableContentInteraction(){
        mLineEditText.setKeyListener(new EditText(this).getKeyListener());
        mLineEditText.setFocusable(true);
        mLineEditText.setFocusableInTouchMode(true);
        mLineEditText.setCursorVisible(true);
        mLineEditText.requestFocus();
    }

    private void hideSoftKeyboard(){
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null){
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }




    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d(TAG, "onDoubleTapEvent: clicked");
        enableEditMode();
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_check:
                hideSoftKeyboard();
                disableEditMode();
                break;

            case R.id.toolbar_back_arrow:
                finish();
                break;

            case R.id.note_text_title:
                enableEditMode();
                mEditTitle.requestFocus();
                mEditTitle.setSelection(mEditTitle.length());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(mMode == EDIT_MODE_ENABLE){
            onClick(mCheck);
        }
        else {
            super.onBackPressed();
        }
    }


    //new
    //when Pause - lifeCycle
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode", mMode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMode = savedInstanceState.getInt("mode");
        if (mMode == EDIT_MODE_ENABLE){
            enableEditMode();
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
        public void afterTextChanged(Editable s) {
        mViewTitle.setText(s.toString());
    }
}
