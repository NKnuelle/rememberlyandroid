package de.rememberly.rememberlyandroidapp.model;

public class Todo extends HttpResponse{
    public String getList_id() {
        return list_id;
    }

    public void setList_id(String list_id) {
        this.list_id = list_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getExpires_on() {
        return expires_on;
    }

    public void setExpires_on(String expires_on) {
        this.expires_on = expires_on;
    }

    public String getTodo_text() {
        return todo_text;
    }

    public void setTodo_text(String todo_text) {
        this.todo_text = todo_text;
    }

    public String getTodo_id() {
        return todo_id;
    }

    public void setTodo_id(String todo_id) {
        this.todo_id = todo_id;
    }

    public String getIs_checked() {
        return is_checked;
    }

    public void setIs_checked(String is_checked) {
        this.is_checked = is_checked;
    }

    public Todo(String list_id, String created_at, String expires_on,
                String todo_text, String todo_id, String is_checked) {
        this.list_id = list_id;
        this.created_at = created_at;
        this.expires_on = expires_on;
        this.todo_text = todo_text;
        this.todo_id = todo_id;
        this.is_checked = is_checked;
    }

    private String list_id;
    private String created_at;
    private String expires_on;
    private String todo_text;
    private String todo_id;
    private String is_checked;
}
