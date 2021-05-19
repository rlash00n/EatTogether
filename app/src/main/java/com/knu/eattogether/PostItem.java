package com.knu.eattogether;

import java.util.ArrayList;

public class PostItem {

    private String title;
    private String contents;
    private String writetime;
    private String postid;
    private String userid;
    private String picturecnt;
    private String cur_people;
    private String max_people;
    private String imageexist;
    private ArrayList<String> imageurilist = new ArrayList<>();
    private ArrayList<String> imagenamelist = new ArrayList<>();

    public PostItem(String title, String contents, String writetime, String postid, String userid, String picturecnt, String cur_people, String max_people, String imageexist, ArrayList<String> imageurilist, ArrayList<String> imagenamelist) {
        this.title = title;
        this.contents = contents;
        this.writetime = writetime;
        this.postid = postid;
        this.userid = userid;
        this.picturecnt = picturecnt;
        this.cur_people = cur_people;
        this.max_people = max_people;
        this.imageexist = imageexist;
        this.imageurilist = imageurilist;
        this.imagenamelist = imagenamelist;
    }

    public PostItem() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getWritetime() {
        return writetime;
    }

    public void setWritetime(String writetime) {
        this.writetime = writetime;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPicturecnt() {
        return picturecnt;
    }

    public void setPicturecnt(String picturecnt) {
        this.picturecnt = picturecnt;
    }

    public String getCur_people() {
        return cur_people;
    }

    public void setCur_people(String cur_people) {
        this.cur_people = cur_people;
    }

    public String getMax_people() {
        return max_people;
    }

    public void setMax_people(String max_people) {
        this.max_people = max_people;
    }

    public String getImageexist() {
        return imageexist;
    }

    public void setImageexist(String imageexist) {
        this.imageexist = imageexist;
    }

    public ArrayList<String> getImageurilist() {
        return imageurilist;
    }

    public void setImageurilist(ArrayList<String> imageurilist) {
        this.imageurilist = imageurilist;
    }

    public ArrayList<String> getImagenamelist() {
        return imagenamelist;
    }

    public void setImagenamelist(ArrayList<String> imagenamelist) {
        this.imagenamelist = imagenamelist;
    }
}
