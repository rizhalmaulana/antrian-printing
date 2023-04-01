package com.rizal.antrianprinting.models.layanan;

public class LayananItem {

    private int id;
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

    @Override
    public String toString() {
        return nama_layanan;
    }
}
