package de.rememberly.rememberlyandroidapp.model;

public class Todolist extends HttpResponse {

    public Todolist(String list_id, String list_name, String created_at, String owner, String isShared) {
        this.list_id = list_id;
        this.list_name = list_name;
        this.created_at = created_at;
        this.owner = owner;
        this.isShared = isShared;
    }
    public Todolist(String list_name) {
        this.list_name = list_name;
    }

    public String getList_id() {
        return list_id;
    }

    public void setList_id(String list_id) {
        this.list_id = list_id;
    }

    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    private String list_name;
    private String created_at;
    private String owner;
    private String list_id;

    public boolean IsShared() {
        return (isShared.equals("1"));
    }

    public void setShared(String isShared) {
        this.isShared = isShared;
    }

    private String isShared;
}
