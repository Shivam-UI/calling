package com.lgt.twowink.Extras;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lgt.twowink.Model.UserDetails;

import java.lang.reflect.Type;

public class SessionManager {

    public static String USER_DETAILS = "USER_DETAILS";
    public static String USER_INFO = "USER_INFO";
    public static String isUserSaved="isUserSaved";
    public static String user_status="user_status";
    private static SharedPreferences.Editor prefsEditor,editor;

    //user details
    public UserDetails getUser(Context mContext) {
        SharedPreferences pref = mContext.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        String userJSONString = pref.getString(USER_INFO, "");
        if (userJSONString == null)
            return null;
        Type type = new TypeToken<UserDetails>() {
        }.getType();
        UserDetails user = new Gson().fromJson(userJSONString, type);
        return user;
    }

    public void setUser(Context mContext, UserDetails user) {
        SharedPreferences pref = mContext.getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
        prefsEditor = pref.edit();
        if (user == null)
            prefsEditor.putString(USER_INFO, null);
        else {
            String userJSONString = new Gson().toJson(user);
            prefsEditor.putString(USER_INFO, userJSONString);
        }
        prefsEditor.apply();
    }

    public static void saveUser(Context context,String status){


        SharedPreferences pref=context.getSharedPreferences(isUserSaved,Context.MODE_PRIVATE);
        editor=pref.edit();
        editor.putString(user_status,status);
        editor.apply();

    }
    public static String isSavedUser(Context context){

        if (context==null){
            return "false";
        }
        SharedPreferences preferences=context.getSharedPreferences(isUserSaved,Context.MODE_PRIVATE);
        return preferences.getString(user_status,"false");
    }

}
