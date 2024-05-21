package com.rizal.antrianprinting.utils;

import com.rizal.antrianprinting.models.antrian.AntrianResponses;
import com.rizal.antrianprinting.models.designer.DesignerResponse;
import com.rizal.antrianprinting.models.layanan.LayananResponse;
import com.rizal.antrianprinting.models.riwayat.RiwayatResponse;
import com.rizal.antrianprinting.models.waktubooking.WaktuBookingResponse;
import com.rizal.antrianprinting.models.waktuselesai.WaktuSelesaiResponse;
import com.rizal.antrianprinting.service.MyFirebaseMessagingService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MobileService {
    @FormUrlEncoded
    @POST("login-mobile")
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

    @PUT("cancel-antrian/{id}")
    Call<Responses> cancelantrian(@Path("id") Integer id);

    @FormUrlEncoded
    @POST("push-notification")
    Call<Responses> pushNotification(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("update-fcm")
    Call<Responses> updateFCMToken(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("register")
    Call<Responses> createuser(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("check-user")
    Call<Responses> checkuser(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("check-antrian")
    Call<Responses> checkantrian(@FieldMap Map<String, String> map);

    @GET("get-antrian/{id}")
    Call<AntrianResponses> getantrian(@Path("id") Integer id);

    @GET("get-riwayat/{id}")
    Call<RiwayatResponse> getriwayat(@Path("id") Integer id);

}
