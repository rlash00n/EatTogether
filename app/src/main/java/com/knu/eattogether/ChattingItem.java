package com.knu.eattogether;

import java.util.ArrayList;

public class ChattingItem {

    private String contents;
    private String senderid;
    private ArrayList<String> receiversid = new ArrayList<>();
    private String time;
    private ArrayList<ArrayList<String>> seen = new ArrayList<ArrayList<String>>();
    private String newchatter;
    private String newid;
    private String outchatter;
    private String outid;
    private int viewType;

    public ChattingItem(String contents, String senderid, ArrayList<String> receiversid, String time, ArrayList<ArrayList<String>> seen, String newchatter, String newid, String outchatter, String outid, int viewType) {
        this.contents = contents;
        this.senderid = senderid;
        this.receiversid = receiversid;
        this.time = time;
        this.seen = seen;
        this.newchatter = newchatter;
        this.newid = newid;
        this.outchatter = outchatter;
        this.outid = outid;
        this.viewType = viewType;
    }

    public ChattingItem() {
    }

    public String getNewchatter() {
        return newchatter;
    }

    public void setNewchatter(String newchatter) {
        this.newchatter = newchatter;
    }

    public String getNewid() {
        return newid;
    }

    public void setNewid(String newid) {
        this.newid = newid;
    }

    public String getOutchatter() {
        return outchatter;
    }

    public void setOutchatter(String outchatter) {
        this.outchatter = outchatter;
    }

    public String getOutid() {
        return outid;
    }

    public void setOutid(String outid) {
        this.outid = outid;
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

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
