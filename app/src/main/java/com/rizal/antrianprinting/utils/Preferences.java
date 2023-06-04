package com.rizal.antrianprinting.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.rizal.antrianprinting.models.antrian.Antrian;
import com.rizal.antrianprinting.models.user.User;

public class Preferences {
    public static User getUser(Context context){
        try{
            String json = getString(context, Static.USER_DATA);
            return new Gson().fromJson(json, User.class);
        }catch (Exception e){
            return null;
        }
    }

    public static void setUser(Context context, User user){
        putString(context, Static.USER_DATA, new Gson().toJson(user));
    }

    public static Antrian getAntrian(Context context){
        try{
            String json = getString(context, Static.ANTRIAN_DATA);
            return new Gson().fromJson(json, Antrian.class);
        }catch (Exception e){
            return null;
        }
    }

    public static void setAntrian(Context context, Antrian antrian){
        putString(context, Static.ANTRIAN_DATA, new Gson().toJson(antrian));
    }

    public static String getToken(Context context){
        return getString(context, Static.TOKEN);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Static.MyPref, Context.MODE_PRIVATE);
        return preferences.edit();
    }

    public static void putString(Context context, String key, String value){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, value).commit();
    }

    public static String getString(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences(Static.MyPref, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static void putInterval(Context context, String key, int value){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(key, value).commit();
    }

    public static void putBoolean(Context context, String key, boolean value){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences(Static.MyPref, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static int getInt(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences(Static.MyPref, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

    public static void setLoginFlag(Context context, boolean flag){
        putBoolean(context, Static.LOGIN_KEY, flag);
    }

    public static void setBookingFlag(Context context, boolean flag){
        putBoolean(context, Static.BOOKING_KEY, flag);
    }

    public static boolean getLoginFlag(Context context){
        return getBoolean(context, Static.LOGIN_KEY);
    }

    public static boolean getBookingFlag(Context context){
        return getBoolean(context, Static.BOOKING_KEY);
    }

}
