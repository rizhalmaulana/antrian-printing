package com.rizal.antrianprinting.models.layanan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Layanan {
    public Layanan() {
    }

    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("nama_layanan")
    private String nama_layanan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama_layanan() {
        return nama_layanan;
    }

    public void setNama_layanan(String nama_layanan) {
        this.nama_layanan = nama_layanan;
    }
}
