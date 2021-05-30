package com.knu.eattogether;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChattingItem2 {

    private String contents;
    private String senderid;
    private ArrayList<String> receiversid = new ArrayList<>();
    private String time;
    private Map<String,Object> seenUsers = new HashMap<>();

    public ChattingItem2(String contents, String senderid, ArrayList<String> receiversid, String time, Map<String, Object> seenUsers) {
        this.contents = contents;
        this.senderid = senderid;
        this.receiversid = receiversid;
        this.time = time;
        this.seenUsers = seenUsers;
    }

    public Map<String, Object> getSeenUsers() {
        return seenUsers;
    }

    public void setSeenUsers(Map<String, Object> seenUsers) {
        this.seenUsers = seenUsers;
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

}
