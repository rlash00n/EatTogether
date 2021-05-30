package com.knu.eattogether.Notification;

public class SendData {

    private String contents;
    private String postid;
    private String senderid;
    private String where;

    public SendData(String contents, String postid, String senderid, String where) {
        this.contents = contents;
        this.postid = postid;
        this.senderid = senderid;
        this.where = where;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }
}
