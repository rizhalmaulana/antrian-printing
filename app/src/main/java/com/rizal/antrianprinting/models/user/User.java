package com.rizal.antrianprinting.models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    public User(){
    }

    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("uid")
    private String uid;
    @Expose
    @SerializedName("username")
    private String username;
    @Expose
    @SerializedName("nama_lengkap")
    private String nama_lengkap;
    @Expose
    @SerializedName("alamat")
    private String alamat;
    @Expose
    @SerializedName("jenis_kelamin")
    private String jenis_kelamin;
    @Expose
    @SerializedName("tempat_lahir")
    private String tempat_lahir;
    @Expose
    @SerializedName("tanggal_lahir")
    private String tanggal_lahir;
    @Expose
    @SerializedName("status")
    private String status;
    @Expose
    @SerializedName("phone_user")
    private String phone_user;
    @Expose
    @SerializedName("email_user")
    private String email_user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNama_lengkap() {
        return nama_lengkap;
    }

    public void setNama_lengkap(String nama_lengkap) {
        this.nama_lengkap = nama_lengkap;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getTempat_lahir() {
        return tempat_lahir;
    }

    public void setTempat_lahir(String tempat_lahir) {
        this.tempat_lahir = tempat_lahir;
    }

    public String getTanggal_lahir() {
        return tanggal_lahir;
    }

    public void setTanggal_lahir(String tanggal_lahir) {
        this.tanggal_lahir = tanggal_lahir;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone_user() {
        return phone_user;
    }

    public void setPhone_user(String phone_user) {
        this.phone_user = phone_user;
    }

    public String getEmail_user() {
        return email_user;
    }

    public void setEmail_user(String email_user) {
        this.email_user = email_user;
    }
}
