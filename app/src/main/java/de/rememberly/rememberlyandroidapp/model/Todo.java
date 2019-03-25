package de.rememberly.rememberlyandroidapp.model;

public class Todo extends HttpResponse{
    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(String expiresOn) {
        this.expiresOn = expiresOn;
    }

    public String getTodoText() {
        return todoText;
    }

    public void setTodoText(String todoText) {
        this.todoText = todoText;
    }

    public String getTodoID() {
        return todoID;
    }

    public void setTodoID(String todoID) {
        this.todoID = todoID;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

    public Todo(String listID, String createdAt, String expiresOn,
                String todoText, String todoID, String isChecked) {
        this.listID = listID;
        this.createdAt = createdAt;
        this.expiresOn = expiresOn;
        this.todoText = todoText;
        this.todoID = todoID;
        this.isChecked = isChecked;
    }

    private String listID;
    private String createdAt;
    private String expiresOn;
    private String todoText;
    private String todoID;
    private String isChecked;
}
