package com.example.android.effectivenavigation;

import java.util.ArrayList;

public class UserHolder {
    private ArrayList<User> users;
    public UserHolder() {
    	users = new ArrayList<User>();
    }
    public void add(User user) {
    	users.add(user);
    }
}
