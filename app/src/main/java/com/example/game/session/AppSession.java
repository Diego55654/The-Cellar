package com.example.game.session;
import android.app.Application;

public class AppSession extends Application {
    private String userId;
    private String userName;
    public void login(String id, String name) {
        this.userId = id;
        this.userName = name;
    }
    public void logout() {
        this.userId = null;

        this.userName = null;
    }
    public String getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    public boolean isLoggedIn() {
        return userId != null;
    }
}

