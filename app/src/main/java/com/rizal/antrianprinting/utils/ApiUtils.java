package com.rizal.antrianprinting.utils;

import android.content.Context;

public class ApiUtils {
    public static String API = Static.LOCALURL;
    public static String URL_FCM = Static.URL_FCM;

    public static MobileService MobileService(Context context){
        return RetrofitClient.getClient(context, API).create(MobileService.class);
    }

    public static MobileService FCMService(Context context){
        return RetrofitClient.getClient(context, URL_FCM).create(MobileService.class);
    }
}
