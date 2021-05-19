package com.knu.eattogether;

import java.util.ArrayList;

public class ChatListItem {

    private String chatid;
    private String postid;
    private String time;
    private ArrayList<String> chatters_list = new ArrayList<>();

    public ChatListItem(String chatid, String postid, String time, ArrayList<String> chatters_list) {
        this.chatid = chatid;
        this.postid = postid;
        this.time = time;
        this.chatters_list = chatters_list;
    }

    public ChatListItem() {
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<String> getChatters_list() {
        return chatters_list;
    }

    public void setChatters_list(ArrayList<String> chatters_list) {
        this.chatters_list = chatters_list;
    }
}
