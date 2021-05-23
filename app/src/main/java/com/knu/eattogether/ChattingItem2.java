package com.knu.eattogether;

import java.util.ArrayList;

public class ChattingItem2 {

    private String contents;
    private String senderid;
    private ArrayList<String> receiversid = new ArrayList<>();
    private String time;
    private ArrayList<ArrayList<String>> seen = new ArrayList<ArrayList<String>>();

    public ChattingItem2(String contents, String senderid, ArrayList<String> receiversid, String time, ArrayList<ArrayList<String>> seen) {
        this.contents = contents;
        this.senderid = senderid;
        this.receiversid = receiversid;
        this.time = time;
        this.seen = seen;
    }

    public ChattingItem2() {
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public ArrayList<String> getReceiversid() {
        return receiversid;
    }

    public void setReceiversid(ArrayList<String> receiversid) {
        this.receiversid = receiversid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<ArrayList<String>> getSeen() {
        return seen;
    }

    public void setSeen(ArrayList<ArrayList<String>> seen) {
        this.seen = seen;
    }
}
