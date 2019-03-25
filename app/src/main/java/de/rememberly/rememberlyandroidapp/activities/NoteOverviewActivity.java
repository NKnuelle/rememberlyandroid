package de.rememberly.rememberlyandroidapp.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.adapter.NoteOverviewAdapter;
import de.rememberly.rememberlyandroidapp.model.HttpResponse;
import de.rememberly.rememberlyandroidapp.model.Note;
import de.rememberly.rememberlyandroidapp.remote.APICall;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.Token;
import de.rememberly.rememberlyandroidapp.remote.IApiCallback;

public class NoteOverviewActivity extends RememberlyStdMenuActivity implements IApiCallback {
    private RecyclerView listRecyclerView;
    private NoteOverviewAdapter noteOverviewAdapter;
    private RecyclerView.LayoutManager listManager;
    private APICall apiCall;
    private ArrayList<Note> noteData = new ArrayList<Note>();
    ;
    private ImageButton addButton;
    private EditText listAddEdittext;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_overview);
        // set background animation:
        LinearLayout linearLayout = findViewById(R.id.AnimationRootLayout);
        super.setupAnimation(linearLayout);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        super.setupStdToolbar(mainToolbar);

        listRecyclerView = findViewById(R.id.listRecyclerView);
        listManager = new LinearLayoutManager(this);
        listAddEdittext = findViewById(R.id.newListItemInput);
        listAddEdittext.setHint(getResources().getString(R.string.inputNewNoteName));
        listRecyclerView.setLayoutManager(listManager);
        addButton = findViewById(R.id.imageButton);
        noteOverviewAdapter = new NoteOverviewAdapter(this, noteData);
        listRecyclerView.setAdapter(noteOverviewAdapter);
        apiCall = new APICall(PreferencesManager.getURL(this));

        initImagebutton();
        initNotes();
        initEditDoneButton();
        setupSwipeAndRefresh();
    }

    private void initImagebutton() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });
    }
    private void initEditDoneButton() {
        listAddEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addNote();
                    return true;
                }
                return false;
            }
        });
    }
    private void addNote() {
        String newNoteText = listAddEdittext.getText().toString();
        if (!newNoteText.isEmpty()) {
            Note newNote = new Note(newNoteText);
            apiCall.newNote(NoteOverviewActivity.this,
                    PreferencesManager.getUserToken(NoteOverviewActivity.this), newNote);
        }
    }

    private void initNotes() {
        String token = PreferencesManager.getUserToken(this);
        apiCall.getNotes(this, token);
    }

    private void setupSwipeAndRefresh() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                apiCall.getNewToken(NoteOverviewActivity.this,
                        PreferencesManager.getUserToken(NoteOverviewActivity.this));
                noteData.clear();
                noteOverviewAdapter.notifyDataSetChanged();
                initNotes();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
    public void onSuccess(int requestCode, HttpResponse httpResponse) {
        if (requestCode == APICall.CREATE_NOTE) {
            Note responseNote = (Note) httpResponse;
            noteData.add(responseNote);
            noteOverviewAdapter.notifyItemInserted(noteData.size() - 1);
            apiCall.getNewToken(NoteOverviewActivity.this,
                    PreferencesManager.getUserToken(NoteOverviewActivity.this));
        }
        if (requestCode == APICall.NEW_TOKEN) {
            Token newToken = (Token) httpResponse;
            PreferencesManager.storeUserToken(newToken.getToken(), NoteOverviewActivity.this);
        }
    }

    public void onFailure(int requestCode, Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
    }

    public void onError(int requestCode, HttpResponse httpResponse) {
        Toast.makeText(this, httpResponse.getMessage(), Toast.LENGTH_LONG).show();
    }

    public void onNotesReceived(List<Note> noteList) {
        noteOverviewAdapter.clear();
        ArrayList<Note> noteArray = (ArrayList<Note>) noteList;
        for (Note note : noteArray) {
            noteData.add(note);
            noteOverviewAdapter.notifyItemInserted(noteData.size() - 1);
        }
    }
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
