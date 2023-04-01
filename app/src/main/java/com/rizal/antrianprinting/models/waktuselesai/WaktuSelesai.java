package com.rizal.antrianprinting.models.waktuselesai;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WaktuSelesai {

    public WaktuSelesai() {
    }

    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("jam_selesai")
    private String jam_selesai;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJam_selesai() {
        return jam_selesai;
    }

    public void setJam_selesai(String jam_selesai) {
        this.jam_selesai = jam_selesai;
    }
}
