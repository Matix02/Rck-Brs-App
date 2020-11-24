package com.example.rckbrswatch2app;

import com.google.firebase.Timestamp;

public class User {
    private String userID;
    private String displayName;
    private String email;
    private String password;
    private Timestamp newLogin;
    private Timestamp lastLogin;

    public User(String userID, String displayName, String email) {
        this.userID = userID;
        this.displayName = displayName;
        this.email = email;
    }

    public User(String userID, String displayName, String email, String password) {
        this.userID = userID;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getNewLogin() {
        return newLogin;
    }

    public void setNewLogin(Timestamp newLogin) {
        this.newLogin = newLogin;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
}
