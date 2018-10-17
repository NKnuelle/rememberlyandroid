package de.rememberly.rememberlyandroidapp.service;

import com.google.gson.JsonObject;

import java.util.List;

import de.rememberly.rememberlyandroidapp.model.Notice;
import de.rememberly.rememberlyandroidapp.model.ReturnMessage;
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

    @GET("api/notices")
    Call<List<Notice>> getNotices(@Header("Authorization") String token);

    @GET("api/todos/{list_id}")
    Call<List<Todo>> getTodos(@Header("Authorization") String token, @Path("list_id") String list_id);

    @POST("api/todo/new")
    Call<Todo> newTodo(@Header("Authorization") String token, @Body Todo newTodo);

    @POST("api/tokenrefresh")
    Call<Token> newToken(@Header("Authorization") String oldToken);

    @POST("api/tokenlogin")
    Call<ReturnMessage> tokenLogin(@Header("Authorization") String userToken);

    @POST("api/todolist/new")
    Call<Todolist> newTodolist(@Header("Authorization") String token, @Body Todolist newTodolist);

    @POST("api/notice/new")
    Call<Notice> newNotice(@Header("Authorization") String token, @Body Notice newNotice);

    @PUT("api/todo/update")
    Call<ReturnMessage> updateTodo(@Header("Authorization") String token, @Body Todo updateTodo);

    @PUT("api/todolist/update")
    Call<ReturnMessage> updateTodolist(@Header("Authorization") String token, @Body Todolist updateTodolist);

    @PUT("api/notice/update")
    Call<ReturnMessage> updateNotice(@Header("Authorization") String token, @Body Notice updateNotice);

    @DELETE("api/todolist/delete/{list_id}")
    Call<ReturnMessage> deleteTodolist(@Header("Authorization") String token, @Path("list_id") String list_id);

    @DELETE("api/notice/delete/{noticeID}")
    Call<ReturnMessage> deleteNotice(@Header("Authorization") String token, @Path("noticeID") String noticeID);

    @POST("api/todolist/share")
    Call<ReturnMessage> shareTodolist(@Header("Authorization") String token, @Body JsonObject json);

    @POST("api/notice/share")
    Call<ReturnMessage> shareNotice(@Header("Authorization") String token, @Body JsonObject json);


}
