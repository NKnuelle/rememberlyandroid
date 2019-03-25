package de.rememberly.rememberlyandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.HttpResponse;
import de.rememberly.rememberlyandroidapp.model.Note;
import de.rememberly.rememberlyandroidapp.remote.APICall;
import de.rememberly.rememberlyandroidapp.remote.IApiCallback;
import in.nashapp.androidsummernote.Summernote;


public class NoteActivity extends AnimationActivity implements IApiCallback {

    private String noteID;
    private String noteContent;
    private String noteName;
    private APICall apiCall;
    private Summernote summernote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        LinearLayout linearLayout = findViewById(R.id.AnimationRootLayout);
        super.setupAnimation(linearLayout);
        Intent intent = getIntent();
        this.noteID = intent.getStringExtra("noteID");
        this.noteContent = intent.getStringExtra("noteContent");
        this.noteName = intent.getStringExtra("noteName");
        this.apiCall = new APICall(PreferencesManager.getURL(this));
        setupEditor();

    }

    private void setupEditor() {
        summernote = (Summernote) findViewById(R.id.summernote);
        summernote.getSettings().setUseWideViewPort(true);
        summernote.setRequestCodeforFilepicker(5);//Any Number which is not being used by other OnResultActivity
        if (noteContent != null && !noteContent.isEmpty()) {
            summernote.setText(noteContent);
        }

        Toolbar editorToolbar = (Toolbar) findViewById(R.id.editorToolbar);
        setSupportActionBar(editorToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    public void onSuccess(int requestCode, HttpResponse httpResponse) {
        if (requestCode == APICall.NOTE_UPDATED) {
            Log.i("Note update: ", "Successful");
        }
    }

    public void onFailure(int requestCode, Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
    }

    public void onError(int requestCode, HttpResponse httpResponse) {
        Toast.makeText(this, httpResponse.getMessage(), Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSave:
                // User chose the "Settings" item, show the app settings UI...
                noteContent = summernote.getText();
                Note newNote = new Note(noteName, noteID, noteContent);
                String token = PreferencesManager.getUserToken(this);
                apiCall.updateNote(this, token, newNote);
                return true;

            case R.id.actionLogout:
                // User chose the "Settings" item, show the app settings UI...
                PreferencesManager.clearCredentials(this);
                Intent intent = new Intent(NoteActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    }
