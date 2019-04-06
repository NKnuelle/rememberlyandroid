package de.rememberly.rememberlyandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import de.rememberly.rememberlyandroidapp.remote.APICall;
import de.rememberly.rememberlyandroidapp.remote.IApiCallback;

public class TodoActivity extends RememberlyStdMenuActivity implements IApiCallback {

    private RecyclerView todoRecyclerView;
    private RecyclerView.Adapter todoAdapter;
    private RecyclerView.LayoutManager todoManager;
    private RecyclerView.LayoutManager doneTodoManager;
    private RecyclerView doneTodoRecyclerView;
    private RecyclerView.Adapter doneTodoAdapter;
    private String listID;
    private ArrayList<Todo> todoData = new ArrayList<Todo>();;
    private ArrayList<Todo> doneTodoData = new ArrayList<Todo>();
    private ImageButton imageButton;
    private Button doneTodoButton;
    private EditText todoEdit;
    private SwipeRefreshLayout swipeContainer;
    private APICall apiCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        // set background animation:
        LinearLayout linearLayout = findViewById(R.id.AnimationRootLayout);
        super.setupAnimation(linearLayout);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        super.setupStdToolbar(mainToolbar);

        todoRecyclerView = findViewById(R.id.todo_recycler_view);
        doneTodoRecyclerView = findViewById(R.id.checkedtodolist);
        imageButton = findViewById(R.id.imageButton);
        doneTodoButton = findViewById(R.id.checkedtodobutton);
        todoEdit = findViewById(R.id.newListItemInput);
        todoManager = new LinearLayoutManager(this);
        doneTodoManager = new LinearLayoutManager(this);
        todoRecyclerView.setLayoutManager(todoManager);
        doneTodoRecyclerView.setLayoutManager(doneTodoManager);
        todoAdapter = new TodoAdapter(todoData);
        doneTodoAdapter = new DoneTodoAdapter(doneTodoData);
        doneTodoRecyclerView.setAdapter(doneTodoAdapter);
        todoRecyclerView.setAdapter(todoAdapter);
        doneTodoButton.setVisibility(View.GONE);
        doneTodoRecyclerView.setVisibility(View.GONE);
        apiCall = new APICall(PreferencesManager.getURL(this));
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
        todo.setIsChecked("1");
        doneTodoData.add(0, todo);
        if (doneTodoData.size() > 0) {
            doneTodoButton.setVisibility(View.VISIBLE);
        }
        doneTodoAdapter.notifyItemInserted(0);
        if (doneTodoData.size() >= 16) {
            doneTodoData.remove(15);
            doneTodoAdapter.notifyItemRemoved(15);
        }
        apiCall.updateTodo(this, PreferencesManager.getUserToken(this), todo);

    }
    public void setTodoUndone(Todo todo, int position) {
        doneTodoData.remove(position);
        doneTodoAdapter.notifyItemRemoved(position);
        if (doneTodoData.size() == 0) {
            doneTodoButton.setVisibility(View.GONE);
        }
        todo.setIsChecked("0");
        todoData.add(0, todo);
        todoAdapter.notifyItemInserted(0);

        apiCall.updateTodo(this, PreferencesManager.getUserToken(this), todo);
    }
    private void initTodos() {
        String token = PreferencesManager.getUserToken(this);
        Intent intent = getIntent();
        listID = intent.getStringExtra("listID");
        apiCall.getTodos(this, token, listID);
    }
    private void addTodo() {
        String newTodoText = todoEdit.getText().toString();
        String token = PreferencesManager.getUserToken(this);
        if (!newTodoText.isEmpty()) {
            Todo newTodo = new Todo(listID, null, null,
                    newTodoText, null, "0");
            apiCall.addTodo(this, token, newTodo);
        }
    }
    public void onSuccess(int requestCode, HttpResponse httpResponse) {
        if (requestCode == APICall.UPDATE_TODO) {
            Log.i("Todo update: ", "Successful");
        }
        if (requestCode == APICall.NEW_TODO) {
            Todo responseTodo = (Todo) httpResponse;
            todoData.add(0, responseTodo);
            todoAdapter.notifyItemInserted(0);
            todoEdit.setText("");
        }
    }

    public void onFailure(int requestCode, Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
    }

    public void onError(int requestCode, HttpResponse httpResponse) {
        Toast.makeText(this, httpResponse.getMessage(), Toast.LENGTH_LONG).show();
    }
    public void onTodosReceived(List<Todo> todoList) {
        ArrayList<Todo> todolistArray = (ArrayList<Todo>) todoList;
        // Add Names from API
        for (Todo todo : todolistArray) {
            if (todo.getIsChecked().equals("0")) {
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
    }
}
