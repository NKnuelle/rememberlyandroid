package de.rememberly.rememberlyandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.adapter.DoneTodoAdapter;
import de.rememberly.rememberlyandroidapp.adapter.TodoAdapter;
import de.rememberly.rememberlyandroidapp.model.ReturnMessage;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        todoRecyclerView = findViewById(R.id.todo_recycler_view);
        doneTodoRecyclerView = findViewById(R.id.checkedtodolist);
        imageButton = findViewById(R.id.imageButton);
        doneTodoButton = findViewById(R.id.checkedtodobutton);
        todoEdit = findViewById(R.id.edittodo);
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
        initTodos();
        initImagebutton();
        initDoneButton();
    }
    public void onPause() {
        super.onPause();
    }
    private void initImagebutton() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTodoText = todoEdit.getText().toString();
                if (!newTodoText.isEmpty()) {
                    Todo newTodo = new Todo(listID, null, null,
                            newTodoText, null, "0");
                    Call<Todo> call = userService.newTodo("Bearer " + ApiUtils.getUserToken(TodoActivity.this), newTodo);
                    call.enqueue(new Callback<Todo>() {
                        @Override
                        public void onResponse(Call<Todo> call, Response<Todo> response) {
                            if (response.isSuccessful()) {
                                Todo responseTodo = response.body();
                                todoData.add(responseTodo);
                                todoAdapter.notifyItemInserted(todoData.size() - 1);
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
    public void setTodoDone(Todo todo, int position) {
        todoData.remove(position);
        todoAdapter.notifyItemRemoved(position);
        todo.setIs_checked("1");
        doneTodoData.add(todo);
        if (doneTodoData.size() > 0) {
            doneTodoButton.setVisibility(View.VISIBLE);
        }
        doneTodoAdapter.notifyItemInserted(doneTodoData.size() - 1);

        String token = "Bearer " + ApiUtils.getUserToken(this);

        Call<ReturnMessage> call = userService.updateTodo(token, todo);
        call.enqueue(new Callback<ReturnMessage>() {
            @Override
            public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                if (response.isSuccessful()) {
                    ReturnMessage returnMessage = response.body();
                    Log.i("Todo Update: ", returnMessage.getMessage());
                } else {
                    Log.e("Todo update failed: ", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ReturnMessage> call, Throwable t) {
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
        todoData.add(todo);
        todoAdapter.notifyItemInserted(todoData.size() - 1);

        String token = "Bearer " + ApiUtils.getUserToken(this);

        Call<ReturnMessage> call = userService.updateTodo(token, todo);
        call.enqueue(new Callback<ReturnMessage>() {
            @Override
            public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                if (response.isSuccessful()) {
                    ReturnMessage returnMessage = response.body();
                    Log.i("Todo Update: ", returnMessage.getMessage());
                } else {
                    Log.e("Todo update failed: ", response.errorBody().toString());
                }
            }


            @Override
            public void onFailure(Call<ReturnMessage> call, Throwable t) {
                Toast.makeText(TodoActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initTodos() {
        String token = ApiUtils.getUserToken(this);
        Intent intent = getIntent();
        listID = intent.getStringExtra("list_id");
        Call<List<Todo>> call = userService.getTodos("Bearer " + token, listID);
        call.enqueue(new Callback<List<Todo>>() {
            @Override
            public void onResponse(Call<List<Todo>> call, Response<List<Todo>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Todo> todolistArray = (ArrayList<Todo>) response.body();
                    // Add Names from API
                    for (Todo todo : todolistArray) {
                        if (todo.getIs_checked().equals("0")) {
                            todoData.add(todo);
                            todoAdapter.notifyItemInserted(todoData.size() - 1);
                        } else {
                            doneTodoData.add(todo);
                            doneTodoAdapter.notifyItemInserted(todoData.size() - 1);
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
}