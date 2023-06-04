package com.rizal.antrianprinting.models.riwayat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RiwayatBookingItem {

    public RiwayatBookingItem() {
    }

    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("id_user")
    private int id_user;
    @Expose
    @SerializedName("nama_designer")
    private String nama_designer;
    @Expose
    @SerializedName("jenis_layanan")
    private String jenis_layanan;
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
    @SerializedName("no_handphone")
    private String no_handphone;
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

    public String getNama_designer() {
        return nama_designer;
    }

    public void setNama_designer(String nama_designer) {
        this.nama_designer = nama_designer;
    }

    public String getJenis_layanan() {
        return jenis_layanan;
    }

    public void setJenis_layanan(String jenis_layanan) {
        this.jenis_layanan = jenis_layanan;
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

    public String getNo_handphone() {
        return no_handphone;
    }

    public void setNo_handphone(String no_handphone) {
        this.no_handphone = no_handphone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
