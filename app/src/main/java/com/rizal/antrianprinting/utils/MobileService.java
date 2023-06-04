package com.rizal.antrianprinting.utils;

import static com.rizal.antrianprinting.utils.Static.CONTENT_TYPE;
import static com.rizal.antrianprinting.utils.Static.SERVER_KEY;

import com.rizal.antrianprinting.models.antrian.AntrianResponses;
import com.rizal.antrianprinting.models.designer.DesignerResponse;
import com.rizal.antrianprinting.models.layanan.LayananResponse;
import com.rizal.antrianprinting.models.notification.NotificationData;
import com.rizal.antrianprinting.models.notification.NotificationItem;
import com.rizal.antrianprinting.models.riwayat.RiwayatResponse;
import com.rizal.antrianprinting.models.waktubooking.WaktuBookingResponse;
import com.rizal.antrianprinting.models.waktuselesai.WaktuSelesaiResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MobileService {
    @FormUrlEncoded
    @POST("login")
    Call<Responses> login(@FieldMap Map<String, String> map);

    @GET("get-layanan")
    Call<LayananResponse> getlayanan();

    @GET("get-jambooking")
    Call<WaktuBookingResponse> getjambooking();

    @GET("get-jamselesai")
    Call<WaktuSelesaiResponse> getjamselesai();

    @GET("get-designer")
    Call<DesignerResponse> getdesigner();

    @FormUrlEncoded
    @POST("create-antrian")
    Call<AntrianResponses> createantrian(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("register")
    Call<Responses> createuser(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("check-user")
    Call<Responses> checkuser(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("check-antrian")
    Call<Responses> checkantrian(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("push-fcm")
    Call<Responses> pushdevice(@FieldMap Map<String, String> map);

    @GET("get-antrian/{id}")
    Call<Responses> getantrian(@Path("id") Integer id);

    @GET("get-riwayat/{id}")
    Call<RiwayatResponse> getriwayat(@Path("id") Integer id);

    @Headers({"Authorization: " + SERVER_KEY, "Content-Type: " + CONTENT_TYPE})
    @POST("fcm/send")
    Call<NotificationData> sendNotification(@Body NotificationItem notification);

}
