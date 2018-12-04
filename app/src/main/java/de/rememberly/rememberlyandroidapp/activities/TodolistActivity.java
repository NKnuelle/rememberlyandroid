package de.rememberly.rememberlyandroidapp.activities;

import android.graphics.drawable.AnimationDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.adapter.TodolistAdapter;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.Todolist;
import de.rememberly.rememberlyandroidapp.model.Token;
import de.rememberly.rememberlyandroidapp.remote.ApiUtils;
import de.rememberly.rememberlyandroidapp.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodolistActivity extends AppCompatActivity {
    private RecyclerView todoRecyclerView;
    private RecyclerView.Adapter todolistAdapter;
    private RecyclerView.LayoutManager todoManager;
    private UserService userService;
    private ArrayList<Todolist> todoData = new ArrayList<Todolist>();;
    private ImageButton imageButton;
    private EditText todoEdit;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_overview);
        // set background animation:
        LinearLayout linearLayout = findViewById(R.id.AnimationRootLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        todoRecyclerView = findViewById(R.id.listRecyclerView);
        todoManager = new LinearLayoutManager(this);
        todoEdit = findViewById(R.id.newListItemInput);
        todoEdit.setHint(getResources().getString(R.string.inputNewListName));
        todoRecyclerView.setLayoutManager(todoManager);
        imageButton = findViewById(R.id.imageButton);
        userService = ApiUtils.getUserService();
        todolistAdapter = new TodolistAdapter(todoData);
        todoRecyclerView.setAdapter(todolistAdapter);
        setupSwipeAndRefresh();
        initImagebutton();
        initTodolists();
    }
    private void initImagebutton() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTodolistText = todoEdit.getText().toString();
                if (!newTodolistText.isEmpty()) {
                    Todolist newTodolist = new Todolist(newTodolistText);
                    Call<Todolist> call = userService.newTodolist("Bearer " + PreferencesManager.getUserToken(TodolistActivity.this), newTodolist);
                    call.enqueue(new Callback<Todolist>() {
                        @Override
                        public void onResponse(Call<Todolist> call, Response<Todolist> response) {
                            if (response.isSuccessful()) {
                                Todolist responseTodolist = response.body();
                                todoData.add(responseTodolist);
                                todolistAdapter.notifyItemInserted(todoData.size() - 1);
                                getAndStoreNewToken();

                            } else {
                                Log.e("Error: ",response.body().toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<Todolist> call, Throwable t) {
                            Toast.makeText(TodolistActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private void initTodolists() {
        String token = PreferencesManager.getUserToken(this);
        Call<List<Todolist>> call = userService.getTodolist("Bearer " + token);
        call.enqueue(new Callback<List<Todolist>>() {
            @Override
            public void onResponse(Call<List<Todolist>> call, Response<List<Todolist>> response) {
                if (response.isSuccessful() && !response.body().isEmpty()) {
                    ArrayList<Todolist> todolistArray = (ArrayList<Todolist>) response.body();
                    for (Todolist todolist : todolistArray) {
                        todoData.add(todolist);
                        todolistAdapter.notifyItemInserted(todoData.size() - 1);
                    }
                } else {
                    Log.e("Error: ",response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<List<Todolist>> call, Throwable t) {

            }
        });
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
                getAndStoreNewToken();
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
    private void getAndStoreNewToken() {
        Call<Token> call = userService.newToken("Bearer " + PreferencesManager.getUserToken(this));
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Token newToken = (Token) response.body();
                    PreferencesManager.storeUserToken(newToken.getToken(), TodolistActivity.this);
                } else {
                    Log.e("Errorcode: ",response.message());
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("Errormessage: ",t.getMessage());
            }
        });
    }
}
