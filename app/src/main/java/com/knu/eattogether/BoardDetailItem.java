package com.knu.eattogether;

import java.util.ArrayList;

public class BoardDetailItem {
    private ArrayList<String> imageurilist = new ArrayList<>();

    public BoardDetailItem(ArrayList<String> imageurilist) {
        this.imageurilist = imageurilist;
    }

    public BoardDetailItem() {
    }

    public ArrayList<String> getImageurilist() {
        return imageurilist;
    }

    public void setImageurilist(ArrayList<String> imageurilist) {
        this.imageurilist = imageurilist;
    }
}
