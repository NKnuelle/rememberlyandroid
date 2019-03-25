package de.rememberly.rememberlyandroidapp.model;


/**
 * Class Note is a Http response model. A notice is sent by the rememberly server in JSON format.
 * All attributes are related to the JSON response.
 */
public class Note extends HttpResponse {

    public Note(String changedAt, String noteName, String noteID, String owner, String isShared, String noteContent, String noteDirectory) {
        this.changedAt = changedAt;
        this.noteName = noteName;
        this.noteID = noteID;
        this.owner = owner;
        this.isShared = isShared;
        this.noteContent = noteContent;
    }

    public Note(String noteName) {
        this.noteName = noteName;
    }

    public Note(String noteName, String noteID, String noteContent) {
        this.noteName = noteName;
        this.noteID = noteID;
        this.noteContent = noteContent;
    }

    public String getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(String changedAt) {
        this.changedAt = changedAt;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getNoteID() {
        return noteID;
    }

    public void setNoteID(String noteID) {
        this.noteID = noteID;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteDirectory() {
        return noteDirectory;
    }

    public void setNoteDirectory(String noteDirectory) {
        this.noteDirectory = noteDirectory;
    }

    private String noteContent;
    private String changedAt;
    private String noteName;
    private String noteID;
    private String owner;
    private String isShared;
    private String noteDirectory;

    public boolean isShared() {
        return (isShared.equals("1"));
    }

    public void setIsShared(String isShared) {
        this.isShared = isShared;
    }


}
