package com.rizal.antrianprinting.models.waktubooking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WaktuBooking {

    public WaktuBooking() {
    }

    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("jam_booking")
    private String jam_booking;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJam_booking() {
        return jam_booking;
    }

    public void setJam_booking(String jam_booking) {
        this.jam_booking = jam_booking;
    }
}
