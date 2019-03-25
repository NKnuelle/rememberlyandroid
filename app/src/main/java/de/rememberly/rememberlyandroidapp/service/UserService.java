package de.rememberly.rememberlyandroidapp.service;

import com.google.gson.JsonObject;

import java.util.List;

import de.rememberly.rememberlyandroidapp.model.Note;
import de.rememberly.rememberlyandroidapp.model.HttpResponse;
import de.rememberly.rememberlyandroidapp.model.Todo;
import de.rememberly.rememberlyandroidapp.model.Todolist;
import de.rememberly.rememberlyandroidapp.model.Token;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @GET("login")
    Call<Token> login(@Header("Authorization") String credentials);

    @GET("api/todolists")
    Call<List<Todolist>> getTodolist(@Header("Authorization") String token);

    @GET("api/notes")
    Call<List<Note>> getNotes(@Header("Authorization") String token);

    @GET("api/todos/{listID}")
    Call<List<Todo>> getTodos(@Header("Authorization") String token, @Path("listID") String listID);

    @POST("api/todo/new")
    Call<Todo> newTodo(@Header("Authorization") String token, @Body Todo newTodo);

    @POST("api/tokenrefresh")
    Call<Token> newToken(@Header("Authorization") String oldToken);

    @POST("api/tokenlogin")
    Call<Token> tokenLogin(@Header("Authorization") String userToken);

    @POST("api/todolist/new")
    Call<Todolist> newTodolist(@Header("Authorization") String token, @Body Todolist newTodolist);

    @POST("api/note/new")
    Call<Note> newNote(@Header("Authorization") String token, @Body Note newNote);

    @PUT("api/todo/update")
    Call<HttpResponse> updateTodo(@Header("Authorization") String token, @Body Todo updateTodo);

    @PUT("api/todolist/update")
    Call<HttpResponse> updateTodolist(@Header("Authorization") String token, @Body Todolist updateTodolist);

    @PUT("api/note/update")
    Call<HttpResponse> updateNote(@Header("Authorization") String token, @Body Note updateNote);

    @DELETE("api/todolist/delete/{listID}")
    Call<HttpResponse> deleteTodolist(@Header("Authorization") String token, @Path("listID") String listID);

    @DELETE("api/note/delete/{noteID}")
    Call<HttpResponse> deleteNote(@Header("Authorization") String token, @Path("noteID") String noteID);

    @POST("api/todolist/share")
    Call<HttpResponse> shareTodolist(@Header("Authorization") String token, @Body JsonObject json);

    @POST("api/note/share")
    Call<HttpResponse> shareNote(@Header("Authorization") String token, @Body JsonObject json);


}
