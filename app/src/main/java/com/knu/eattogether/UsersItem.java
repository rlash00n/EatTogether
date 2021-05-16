package com.knu.eattogether;

public class UsersItem {
    private String email;
    private String idToken;
    private String nickname;
    private String password;
    private String profileuri;
    private String profileimagename;

    public UsersItem() {
    }

    public UsersItem(String email, String idToken, String nickname, String password, String profileuri, String profileimagename) {
        this.email = email;
        this.idToken = idToken;
        this.nickname = nickname;
        this.password = password;
        this.profileuri = profileuri;
        this.profileimagename = profileimagename;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileuri() {
        return profileuri;
    }

    public void setProfileuri(String profileuri) {
        this.profileuri = profileuri;
    }

    public String getProfileimagename() {
        return profileimagename;
    }

    public void setProfileimagename(String profileimagename) {
        this.profileimagename = profileimagename;
    }
}
