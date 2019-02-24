package de.rememberly.rememberlyandroidapp.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.adapter.DoneTodoAdapter;
import de.rememberly.rememberlyandroidapp.adapter.TodoAdapter;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.HttpResponse;
import de.rememberly.rememberlyandroidapp.model.Todo;
import de.rememberly.rememberlyandroidapp.remote.ApiUtils;
import de.rememberly.rememberlyandroidapp.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoActivity extends AppCompatActivity {

    private RecyclerView todoRecyclerView;
    private RecyclerView.Adapter todoAdapter;
    private RecyclerView.LayoutManager todoManager;
    private RecyclerView.LayoutManager doneTodoManager;
    private RecyclerView doneTodoRecyclerView;
    private RecyclerView.Adapter doneTodoAdapter;
    private UserService userService;
    private String listID;
    private ArrayList<Todo> todoData = new ArrayList<Todo>();;
    private ArrayList<Todo> doneTodoData = new ArrayList<Todo>();
    private ImageButton imageButton;
    private Button doneTodoButton;
    private EditText todoEdit;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        // set background animation:
        NestedScrollView scrollView = findViewById(R.id.AnimationRootLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) scrollView.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        todoRecyclerView = findViewById(R.id.todo_recycler_view);
        doneTodoRecyclerView = findViewById(R.id.checkedtodolist);
        imageButton = findViewById(R.id.imageButton);
        doneTodoButton = findViewById(R.id.checkedtodobutton);
        todoEdit = findViewById(R.id.newListItemInput);
        todoManager = new LinearLayoutManager(this);
        doneTodoManager = new LinearLayoutManager(this);
        todoRecyclerView.setLayoutManager(todoManager);
        doneTodoRecyclerView.setLayoutManager(doneTodoManager);
        userService = ApiUtils.getUserService();
        todoAdapter = new TodoAdapter(todoData);
        doneTodoAdapter = new DoneTodoAdapter(doneTodoData);
        doneTodoRecyclerView.setAdapter(doneTodoAdapter);
        todoRecyclerView.setAdapter(todoAdapter);
        doneTodoButton.setVisibility(View.GONE);
        doneTodoRecyclerView.setVisibility(View.GONE);
        setupSwipeAndRefresh();
        initTodos();
        initImagebutton();
        initDoneButton();
        initEditDoneButton();
    }
    public void onPause() {
        super.onPause();
    }
    private void initImagebutton() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTodo();
            }
        });
    }
    private void initEditDoneButton() {
        todoEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addTodo();
                    return true;
                }
                return false;
            }
        });
    }
    private void initDoneButton() {
        doneTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneTodoRecyclerView.getVisibility() == View.GONE) {
                    doneTodoRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    doneTodoRecyclerView.setVisibility(View.GONE);
                }
            }
        });
    }
    private void setupSwipeAndRefresh() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                todoData.clear();
                doneTodoData.clear();
                todoAdapter.notifyDataSetChanged();
                doneTodoAdapter.notifyDataSetChanged();
                initTodos();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
    public void setTodoDone(Todo todo, int position) {
        todoData.remove(position);
        todoAdapter.notifyItemRemoved(position);
        todo.setIs_checked("1");
        doneTodoData.add(0, todo);
        if (doneTodoData.size() > 0) {
            doneTodoButton.setVisibility(View.VISIBLE);
        }
        doneTodoAdapter.notifyItemInserted(0);
        if (doneTodoData.size() >= 16) {
            doneTodoData.remove(15);
            doneTodoAdapter.notifyItemRemoved(15);
        }

        String token = "Bearer " + PreferencesManager.getUserToken(this);

        Call<HttpResponse> call = userService.updateTodo(token, todo);
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
                if (response.isSuccessful()) {
                    HttpResponse httpResponse = response.body();
                    Log.i("Todo Update: ", httpResponse.getMessage());
                } else {
                    Log.e("Todo update failed: ", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<HttpResponse> call, Throwable t) {
                Toast.makeText(TodoActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setTodoUndone(Todo todo, int position) {
        doneTodoData.remove(position);
        doneTodoAdapter.notifyItemRemoved(position);
        if (doneTodoData.size() == 0) {
            doneTodoButton.setVisibility(View.GONE);
        }
        todo.setIs_checked("0");
        todoData.add(0, todo);
        todoAdapter.notifyItemInserted(0);

        String token = "Bearer " + PreferencesManager.getUserToken(this);

        Call<HttpResponse> call = userService.updateTodo(token, todo);
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
                if (response.isSuccessful()) {
                    HttpResponse httpResponse = response.body();
                    Log.i("Todo Update: ", httpResponse.getMessage());
                } else {
                    Log.e("Todo update failed: ", response.errorBody().toString());
                }
            }


            @Override
            public void onFailure(Call<HttpResponse> call, Throwable t) {
                Toast.makeText(TodoActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initTodos() {
        String token = PreferencesManager.getUserToken(this);
        Intent intent = getIntent();
        listID = intent.getStringExtra("list_id");
        Call<List<Todo>> call = userService.getTodos("Bearer " + token, listID);
        call.enqueue(new Callback<List<Todo>>() {
            @Override
            public void onResponse(Call<List<Todo>> call, Response<List<Todo>> response) {
                if (response.isSuccessful() && !response.body().isEmpty()) {
                    ArrayList<Todo> todolistArray = (ArrayList<Todo>) response.body();
                    // Add Names from API
                    for (Todo todo : todolistArray) {
                        if (todo.getIs_checked().equals("0")) {
                            todoData.add(0, todo);
                            todoAdapter.notifyItemInserted(0);
                        } else {
                            doneTodoData.add(0, todo);
                            doneTodoAdapter.notifyItemInserted(0);
                        }

                    }
                    if (doneTodoData.size() > 0) {
                        doneTodoButton.setVisibility(View.VISIBLE);
                    }


                } else {
                    Log.e("Error: ", Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Todo>> call, Throwable t) {
                Toast.makeText(TodoActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addTodo() {
        String newTodoText = todoEdit.getText().toString();
        if (!newTodoText.isEmpty()) {
            Todo newTodo = new Todo(listID, null, null,
                    newTodoText, null, "0");
            Call<Todo> call = userService.newTodo("Bearer " + PreferencesManager.getUserToken(TodoActivity.this), newTodo);
            call.enqueue(new Callback<Todo>() {
                @Override
                public void onResponse(Call<Todo> call, Response<Todo> response) {
                    if (response.isSuccessful()) {
                        Todo responseTodo = response.body();
                        todoData.add(0, responseTodo);
                        todoAdapter.notifyItemInserted(0);
                        todoEdit.setText("");
                    } else {
                        Log.e("Error: ",response.body().toString());
                    }
                }

                @Override
                public void onFailure(Call<Todo> call, Throwable t) {
                    Toast.makeText(TodoActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
