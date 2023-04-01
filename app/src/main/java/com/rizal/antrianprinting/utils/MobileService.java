package com.rizal.antrianprinting.utils;

import com.rizal.antrianprinting.models.designer.DesignerResponse;
import com.rizal.antrianprinting.models.layanan.LayananResponse;
import com.rizal.antrianprinting.models.waktubooking.WaktuBookingResponse;
import com.rizal.antrianprinting.models.waktuselesai.WaktuSelesaiResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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
    Call<Responses> createantrian(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("check-antrian")
    Call<Responses> checkantrian(@FieldMap Map<String, String> map);

    @GET("get-antrian/{id}")
    Call<Responses> getantrian(@Path("id") Integer id);
}
