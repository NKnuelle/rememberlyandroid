package de.rememberly.rememberlyandroidapp.model;

public class Notice {
    public Notice(String createdAt, String noticeName, String noticeID, String owner) {
        this.createdAt = createdAt;
        this.noticeName = noticeName;
        this.noticeID = noticeID;
        this.owner = owner;
    }

    public Notice(String noticeName) {
        this.noticeName = noticeName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getNoticeName() {
        return noticeName;
    }

    public void setNoticeName(String noticeName) {
        this.noticeName = noticeName;
    }

    public String getNoticeID() {
        return noticeID;
    }

    public void setNoticeID(String noticeID) {
        this.noticeID = noticeID;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    private String createdAt;
    private String noticeName;
    private String noticeID;
    private String owner;

}
