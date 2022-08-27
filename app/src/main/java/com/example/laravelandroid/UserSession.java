package com.example.laravelandroid;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {

    private static UserSession userSession;
    final SharedPreferences sharedPreferences;
    final SharedPreferences.Editor editor;

    private UserSession(Context context) {
        sharedPreferences = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized UserSession getInstance(Context context) {
        if (userSession == null) {
            userSession = new UserSession(context);
        }
        return userSession;
    }


    public void setUserDetails(boolean isLoggedIn, String userToken){
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.putString("token", userToken);
        editor.apply();
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean("isLoggedIn",false);
    }

    public String getUserToken(){
        return sharedPreferences.getString("token",null);
    }

    public void userLogout() {
        editor.clear();
        editor.apply();
    }

}
