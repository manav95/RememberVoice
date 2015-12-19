package com.example.android.effectivenavigation;

public class User {
	 private String uName;
	 private String uPassword;
     public User(String username, String password) {
    	   uName = username;
    	   uPassword = password;
     }
     public String getUsername() {
    	 return uName;
     }
     public String getPassword() {
    	 return uPassword;
     }
}
