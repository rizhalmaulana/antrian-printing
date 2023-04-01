package com.rizal.antrianprinting.utils;

import android.content.Context;

public class ApiUtils {
    public static String API = Static.LOCALURL;

    public static MobileService MobileService(Context context){
        return RetrofitClient.getClient(context, API).create(MobileService.class);
    }
}
