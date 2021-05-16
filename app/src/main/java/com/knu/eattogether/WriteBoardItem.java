package com.knu.eattogether;

import android.net.Uri;

public class WriteBoardItem {
    private Uri imageuri;
    private int viewtype;

    public WriteBoardItem(Uri imageuri, int viewtype) {
        this.imageuri = imageuri;
        this.viewtype = viewtype;
    }

    public Uri getImageuri() {
        return imageuri;
    }

    public void setImageuri(Uri imageuri) {
        this.imageuri = imageuri;
    }

    public int getViewtype() {
        return viewtype;
    }

    public void setViewtype(int viewtype) {
        this.viewtype = viewtype;
    }
}
