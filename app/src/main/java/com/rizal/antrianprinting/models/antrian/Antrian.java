package com.rizal.antrianprinting.models.antrian;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Antrian {

    public Antrian() {
    }

    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("id_user")
    private int id_user;
    @Expose
    @SerializedName("id_layanan")
    private int id_layanan;
    @Expose
    @SerializedName("id_designer")
    private int id_designer;
    @Expose
    @SerializedName("username")
    private String username;
    @Expose
    @SerializedName("jam_booking")
    private String jam_booking;
    @Expose
    @SerializedName("jam_selesai")
    private String jam_selesai;
    @Expose
    @SerializedName("tgl_pesanan")
    private String tgl_pesanan;
    @Expose
    @SerializedName("phone_user")
    private String phone_user;
    @Expose
    @SerializedName("status")
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_layanan() {
        return id_layanan;
    }

    public void setId_layanan(int id_layanan) {
        this.id_layanan = id_layanan;
    }

    public int getId_designer() {
        return id_designer;
    }

    public void setId_designer(int id_designer) {
        this.id_designer = id_designer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJam_booking() {
        return jam_booking;
    }

    public void setJam_booking(String jam_booking) {
        this.jam_booking = jam_booking;
    }

    public String getJam_selesai() {
        return jam_selesai;
    }

    public void setJam_selesai(String jam_selesai) {
        this.jam_selesai = jam_selesai;
    }

    public String getTgl_pesanan() {
        return tgl_pesanan;
    }

    public void setTgl_pesanan(String tgl_pesanan) {
        this.tgl_pesanan = tgl_pesanan;
    }

    public String getPhone_user() {
        return phone_user;
    }

    public void setPhone_user(String phone_user) {
        this.phone_user = phone_user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
