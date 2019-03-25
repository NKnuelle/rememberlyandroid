package de.rememberly.rememberlyandroidapp.remote;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import de.rememberly.rememberlyandroidapp.activities.NoteOverviewActivity;
import de.rememberly.rememberlyandroidapp.activities.TodoActivity;
import de.rememberly.rememberlyandroidapp.activities.TodolistActivity;
import de.rememberly.rememberlyandroidapp.adapter.NoteOverviewAdapter;
import de.rememberly.rememberlyandroidapp.adapter.TodolistAdapter;
import de.rememberly.rememberlyandroidapp.model.HttpResponse;
import de.rememberly.rememberlyandroidapp.model.Note;
import de.rememberly.rememberlyandroidapp.model.Todo;
import de.rememberly.rememberlyandroidapp.model.Todolist;
import de.rememberly.rememberlyandroidapp.model.Token;
import de.rememberly.rememberlyandroidapp.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static de.rememberly.rememberlyandroidapp.remote.ApiUtils.getTokenString;

/**
 * Created by nilsk on 14.11.2018.
 */


public class APICall {

    private UserService userService;

    /** This constant shows the activity that the callback for login call should be used. */
    public static final int LOGIN_REQUEST = 100;

    public static final int TOKEN_LOGIN = 200;

    public static final int NEW_TOKEN = 300;

    public static final int UPDATE_TODO = 400;

    public static final int GET_TODOS = 500;

    public static final int NEW_TODO = 600;

    public static final int NEW_TODOLIST = 700;

    public static final int GET_TODOLISTS = 800;

    public static final int TODOLIST_DELETED = 900;

    public static final int TODOLIST_SHARED = 1000;

    public static final int TODOLIST_UPDATED = 1100;

    public static final int NOTE_SHARED = 1200;

    public static final int NOTE_UPDATED = 1300;

    public static final int NOTE_DELETED = 1400;

    /** This constants shows the activity that the callback for creating notes should be used. */
    public static final int CREATE_NOTE = 1500;

    public static final int GET_NOTES = 1600;



    public APICall(String url) {
        this.userService = ApiUtils.getUserService(url);
    }

    public void autoLogin(final IApiCallback iApiCallback, final String userToken, final String username, final String password) {
        Call<Token> call = userService.tokenLogin(getTokenString(userToken));
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                if (response.isSuccessful()) {
                    iApiCallback.onSuccess(TOKEN_LOGIN, response.body());
                } else {
                    userLogin(iApiCallback, username, password);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Token> call,@NonNull Throwable t) {
                iApiCallback.onFailure(TOKEN_LOGIN, t);
            }
        });
    }
    public void userLogin(final IApiCallback iApiCallback, final String username, final String password) {
        final String credentials = ApiUtils.getCredentialString(username, password);
        Call<Token> call = userService.login(credentials);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call,@NonNull Response<Token> response) {
                if (response.isSuccessful()) {
                    iApiCallback.onSuccess(LOGIN_REQUEST, response.body());
                } else {
                    iApiCallback.onError(LOGIN_REQUEST, response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Token> call,@NonNull Throwable t) {
                iApiCallback.onFailure(LOGIN_REQUEST, t);
            }
        });
    }
    public void newNote(final IApiCallback iApiCallback, final String token, Note note) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<Note> call = userService.newNote(tokenString, note);
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(@NonNull Call<Note> call, @NonNull Response<Note> response) {
                if (response.isSuccessful()) {
                    iApiCallback.onSuccess(CREATE_NOTE, response.body());

                } else {
                    iApiCallback.onError(CREATE_NOTE, response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Note> call, @NonNull Throwable t) {
                iApiCallback.onFailure(CREATE_NOTE, t);
            }
        });
    }

    public void getNotes(final NoteOverviewActivity noteOverviewActivity, final String token) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<List<Note>> call = userService.getNotes(tokenString);
        call.enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(@NonNull Call<List<Note>> call, @NonNull Response<List<Note>> response) {
                if (response.isSuccessful()) {
                    noteOverviewActivity.onNotesReceived(response.body());
                } else {
                    //TODO: Handle error in (a refactored) onError Callback
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Note>> call,@NonNull Throwable t) {
                noteOverviewActivity.onFailure(GET_NOTES, t);
            }
        });
    }
    public void getNewToken(final IApiCallback iApiCallback, final String token) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<Token> call = userService.newToken(tokenString);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call,@NonNull Response<Token> response) {
                if (response.isSuccessful()) {
                    iApiCallback.onSuccess(NEW_TOKEN, response.body());
                } else {
                    iApiCallback.onError(NEW_TOKEN, response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Token> call,@NonNull Throwable t) {
                iApiCallback.onFailure(NEW_TOKEN, t);
            }
        });
    }
    public void updateTodo(final IApiCallback iApiCallback, final String token, Todo todo) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<HttpResponse> call = userService.updateTodo(tokenString, todo);
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(@NonNull Call<HttpResponse> call,@NonNull Response<HttpResponse> response) {
                if (response.isSuccessful()) {
                    iApiCallback.onSuccess(UPDATE_TODO, response.body());
                } else {
                    iApiCallback.onError(UPDATE_TODO, response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<HttpResponse> call,@NonNull Throwable t) {
                iApiCallback.onFailure(UPDATE_TODO, t);
            }
        });
    }
    public void getTodos(final TodoActivity todoActivity, final String token, final String listID) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<List<Todo>> call = userService.getTodos(tokenString, listID);
        call.enqueue(new Callback<List<Todo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Todo>> call,@NonNull Response<List<Todo>> response) {
                if (response.isSuccessful()) {
                    todoActivity.onTodosReceived(response.body());
                } else {
                    //TODO: Handle error in (a refactored) onError Callback
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Todo>> call,@NonNull Throwable t) {
                todoActivity.onFailure(GET_TODOS, t);
            }
        });
    }
    public void addTodo(final IApiCallback iApiCallback, String token, Todo newTodo) {
        String tokenString = ApiUtils.getTokenString(token);
    Call<Todo> call = userService.newTodo(tokenString, newTodo);
            call.enqueue(new Callback<Todo>() {
        @Override
        public void onResponse(@NonNull Call<Todo> call,@NonNull Response<Todo> response) {
            if (response.isSuccessful()) {
                iApiCallback.onSuccess(NEW_TODO, response.body());
            } else {
                iApiCallback.onError(NEW_TODO, response.body());
            }
        }

        @Override
        public void onFailure(@NonNull Call<Todo> call,@NonNull Throwable t) {
            iApiCallback.onFailure(NEW_TODO, t);
        }
    });
}
    public void newTodolist(final IApiCallback iApiCallback, String token, Todolist newTodolist) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<Todolist> call = userService.newTodolist(tokenString, newTodolist);
        call.enqueue(new Callback<Todolist>() {
            @Override
            public void onResponse(@NonNull Call<Todolist> call,@NonNull Response<Todolist> response) {
                if (response.isSuccessful()) {
                    iApiCallback.onSuccess(NEW_TODOLIST, response.body());

                } else {
                   iApiCallback.onError(NEW_TODOLIST, response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Todolist> call,@NonNull Throwable t) {
                iApiCallback.onFailure(NEW_TODOLIST, t);
            }
        });
    }

    public void getTodolists(final TodolistActivity todolistActivity, String token) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<List<Todolist>> call = userService.getTodolist(tokenString);
        call.enqueue(new Callback<List<Todolist>>() {
            @Override
            public void onResponse(@NonNull Call<List<Todolist>> call,@NonNull Response<List<Todolist>> response) {
                if (response.isSuccessful()) {
                    todolistActivity.onTodolistsReceived(response.body());
                    } else {
                    //TODO: Handle error in (a refactored) onError Callback
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Todolist>> call,@NonNull Throwable t) {
                todolistActivity.onFailure(GET_TODOLISTS, t);
            }
        });
    }
    public void shareNote(final NoteOverviewAdapter noteOverviewAdapter, String token, JsonObject sharedJSON, final int position) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<HttpResponse> call = userService.shareNote(tokenString, sharedJSON);
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(@NonNull Call<HttpResponse> call,@NonNull Response<HttpResponse> response) {
                if (response.isSuccessful()) {
                    noteOverviewAdapter.onNoteShared(NOTE_SHARED, response.body(), position);
                } else {
                    noteOverviewAdapter.onError(NOTE_SHARED, response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<HttpResponse> call,@NonNull Throwable t) {
                noteOverviewAdapter.onFailure(NOTE_SHARED, t);
            }
        });
    }
    public void updateNote(final NoteOverviewAdapter noteOverviewAdapter, String token, Note note, final int position) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<HttpResponse> call = userService.updateNote(tokenString, note);
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(@NonNull Call<HttpResponse> call,@NonNull Response<HttpResponse> response) {
                if (response.isSuccessful()) {
                    noteOverviewAdapter.onNoteUpdated(NOTE_UPDATED, response.body(), position);
                }
                noteOverviewAdapter.onError(NOTE_UPDATED, response.body());
            }

            @Override
            public void onFailure(@NonNull Call<HttpResponse> call,@NonNull Throwable t) {
                noteOverviewAdapter.onFailure(NOTE_UPDATED, t);
            }
        });
    }
    public void updateNote(final IApiCallback iApiCallback, String token, Note note) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<HttpResponse> call = userService.updateNote(tokenString, note);
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(@NonNull Call<HttpResponse> call,@NonNull Response<HttpResponse> response) {
                if (response.isSuccessful()) {
                    iApiCallback.onSuccess(NOTE_UPDATED, response.body());
            }
                iApiCallback.onError(NOTE_UPDATED, response.body());
            }

            @Override
            public void onFailure(@NonNull Call<HttpResponse> call,@NonNull Throwable t) {
                iApiCallback.onFailure(NOTE_UPDATED, t);
            }
        });
    }
    public void deleteNote(final NoteOverviewAdapter noteOverviewAdapter, String token, Note note, final int position) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<HttpResponse> call = userService.deleteNote(tokenString, note.getNoteID());
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(@NonNull Call<HttpResponse> call,@NonNull Response<HttpResponse> response) {
                if (response.isSuccessful()) {
                    noteOverviewAdapter.onNoteDeleted(NOTE_DELETED, response.body(), position);
                } else {
                    noteOverviewAdapter.onError(NOTE_DELETED, response.body());
                }
            }


            @Override
            public void onFailure(@NonNull Call<HttpResponse> call,@NonNull Throwable t) {
                noteOverviewAdapter.onFailure(NOTE_DELETED, t);
            }
        });
    }
    public void deleteTodolist(final TodolistAdapter todolistAdapter, String token, Todolist todolist, final int position) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<HttpResponse> call = userService.deleteTodolist(tokenString, todolist.getListID());
        Log.i("List ID: ", todolist.getListID());
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
                if (response.isSuccessful()) {
                    todolistAdapter.onTodolistDeleted(TODOLIST_DELETED, response.body(), position);
                } else {
                    todolistAdapter.onError(TODOLIST_DELETED, response.body());
                }
            }


            @Override
            public void onFailure(Call<HttpResponse> call, Throwable t) {
                todolistAdapter.onFailure(TODOLIST_DELETED, t);
            }
        });
    }
    public void shareTodolist(final TodolistAdapter todolistAdapter, String token, JsonObject jsonObject, final int position) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<HttpResponse> call = userService.shareTodolist(tokenString, jsonObject);
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
                if (response.isSuccessful()) {
                    todolistAdapter.onTodolistShared(TODOLIST_SHARED, response.body(), position);
                } else {
                    HttpResponse error = new Gson().fromJson(response.errorBody().charStream(), HttpResponse.class);
                    todolistAdapter.onError(TODOLIST_SHARED, error);
                }

            }
            @Override
            public void onFailure(Call<HttpResponse> call, Throwable t) {
                todolistAdapter.onFailure(TODOLIST_SHARED, t);
            }
        });
    }
    public void updateTodolist(final TodolistAdapter todolistAdapter, String token, Todolist todolist, final int position) {
        String tokenString = ApiUtils.getTokenString(token);
        Call<HttpResponse> call = userService.updateTodolist(tokenString, todolist);
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
                if (response.isSuccessful()) {
                    todolistAdapter.onTodolistUpdated(TODOLIST_UPDATED, response.body(), position);

                } else {
                    HttpResponse error = new Gson().fromJson(response.errorBody().charStream(), HttpResponse.class);
                    todolistAdapter.onError(TODOLIST_UPDATED, error);
                }
            }
            @Override
            public void onFailure(Call<HttpResponse> call, Throwable t) {
                todolistAdapter.onFailure(TODOLIST_UPDATED, t);
            }
        });
    }
}