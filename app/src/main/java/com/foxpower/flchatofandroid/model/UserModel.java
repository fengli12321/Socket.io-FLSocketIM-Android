package com.foxpower.flchatofandroid.model;

/**
 * Created by fengli on 2018/2/10.
 */

public class UserModel {

    private String name;
    private boolean isOnline;

    public void setName(String name) {
        this.name = name;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getName() {

        return name;
    }

    public boolean isOnline() {
        return isOnline;
    }
}
