package com.knu.eattogether.Notification;

import java.util.ArrayList;

public class Token {

    private ArrayList<String> TokenList = new ArrayList<>();

    public Token() {
    }

    public Token(ArrayList<String> tokenList) {
        TokenList = tokenList;
    }

    public ArrayList<String> getTokenList() {
        return TokenList;
    }

    public void setTokenList(ArrayList<String> tokenList) {
        TokenList = tokenList;
    }

}

