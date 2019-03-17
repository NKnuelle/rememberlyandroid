package de.rememberly.rememberlyandroidapp.model;

public class Todolist extends HttpResponse {

    public Todolist(String listID, String listName, String createdAt, String owner, String isShared) {
        this.listID = listID;
        this.listName = listName;
        this.createdAt = createdAt;
        this.owner = owner;
        this.isShared = isShared;
    }
    public Todolist(String listName) {
        this.listName = listName;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    private String listName;
    private String createdAt;
    private String owner;
    private String listID;

    public boolean IsShared() {
        return (isShared.equals("1"));
    }

    public void setShared(String isShared) {
        this.isShared = isShared;
    }

    private String isShared;
}
