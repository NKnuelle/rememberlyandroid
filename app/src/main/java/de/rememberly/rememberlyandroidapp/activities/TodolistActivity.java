package de.rememberly.rememberlyandroidapp.activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
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
import de.rememberly.rememberlyandroidapp.adapter.TodolistAdapter;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.HttpResponse;
import de.rememberly.rememberlyandroidapp.model.Todolist;
import de.rememberly.rememberlyandroidapp.model.Token;
import de.rememberly.rememberlyandroidapp.remote.APICall;
import de.rememberly.rememberlyandroidapp.remote.IApiCallback;

public class TodolistActivity extends RememberlyStdMenuActivity implements IApiCallback {
    private RecyclerView todoRecyclerView;
    private RecyclerView.Adapter todolistAdapter;
    private RecyclerView.LayoutManager todoManager;
    private ArrayList<Todolist> todoData = new ArrayList<Todolist>();
    private ImageButton imageButton;
    private EditText todoEdit;
    private APICall apiCall;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_overview);
        // set background animation:
        LinearLayout linearLayout = findViewById(R.id.AnimationRootLayout);
        super.setupAnimation(linearLayout);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        mainToolbar.setTitle(R.string.todolists);
        super.setupStdToolbar(mainToolbar);

        todoRecyclerView = findViewById(R.id.listRecyclerView);
        todoManager = new LinearLayoutManager(this);
        todoEdit = findViewById(R.id.newListItemInput);
        todoEdit.setHint(getResources().getString(R.string.inputNewListName));
        todoRecyclerView.setLayoutManager(todoManager);
        imageButton = findViewById(R.id.imageButton);
        todolistAdapter = new TodolistAdapter(this, todoData);
        todoRecyclerView.setAdapter(todolistAdapter);
        apiCall = new APICall(PreferencesManager.getURL(this));
        setupSwipeAndRefresh();
        initImagebutton();
        initTodolists();
        initEditDoneButton();
    }

    private void initImagebutton() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTodolist();
            }
        });
    }
    private void initEditDoneButton() {
        todoEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addTodolist();
                    return true;
                }
                return false;
            }
        });
    }
    private void addTodolist() {
        String newTodolistText = todoEdit.getText().toString();
        if (!newTodolistText.isEmpty()) {
            Todolist newTodolist = new Todolist(newTodolistText);
            String token = PreferencesManager.getUserToken(TodolistActivity.this);
            apiCall.newTodolist(TodolistActivity.this, token, newTodolist);
        }
    }

    private void initTodolists() {
        String token = PreferencesManager.getUserToken(this);
        apiCall.getTodolists(this, token);
    }

    private void setupSwipeAndRefresh() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                apiCall.getNewToken(TodolistActivity.this,
                        PreferencesManager.getUserToken(TodolistActivity.this));
                todoData.clear();
                todolistAdapter.notifyDataSetChanged();
                initTodolists();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void onSuccess(int requestCode, HttpResponse httpResponse) {
        if (requestCode == APICall.NEW_TODOLIST) {
            Todolist responseTodolist = (Todolist) httpResponse;
            todoData.add(0, responseTodolist);
            todolistAdapter.notifyItemInserted(todoData.size() - 1);
            apiCall.getNewToken(this, PreferencesManager.getUserToken(this));
            todoEdit.setText("");
        }
        if (requestCode == APICall.NEW_TOKEN) {
            Token newToken = (Token) httpResponse;
            PreferencesManager.storeUserToken(newToken.getToken(), TodolistActivity.this);
        }

    }

    public void onFailure(int requestCode, Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
    }

    public void onError(int requestCode, HttpResponse httpResponse) {
        Toast.makeText(this, httpResponse.getMessage(), Toast.LENGTH_LONG).show();
    }

    public void onTodolistsReceived(List<Todolist> todolists) {
        ArrayList<Todolist> todolistArray = (ArrayList<Todolist>) todolists;
        for (Todolist todolist : todolistArray) {
            todoData.add(todolist);
            todolistAdapter.notifyItemInserted(todoData.size() - 1);
        }
    }
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
