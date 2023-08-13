package com.rizal.antrianprinting.models.antrian;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AntrianFlagging {

    public AntrianFlagging() {
    }

    @Expose
    @SerializedName("id_antrian")
    private int id_antrian;
    @Expose
    @SerializedName("total_jam_booking")
    private String total_jam_booking;
    @Expose
    @SerializedName("jam_reminder")
    private String jam_reminder;
    @Expose
    @SerializedName("time_schedule")
    private String time_schedule;
    @Expose
    @SerializedName("status_flagging")
    private int status_flagging;

    public int getId_antrian() {
        return id_antrian;
    }

    public void setId_antrian(int id_antrian) {
        this.id_antrian = id_antrian;
    }

    public String getTotal_jam_booking() {
        return total_jam_booking;
    }

    public void setTotal_jam_booking(String total_jam_booking) {
        this.total_jam_booking = total_jam_booking;
    }

    public String getJam_reminder() {
        return jam_reminder;
    }

    public void setJam_reminder(String jam_reminder) {
        this.jam_reminder = jam_reminder;
    }

    public String getTime_schedule() {
        return time_schedule;
    }

    public void setTime_schedule(String time_schedule) {
        this.time_schedule = time_schedule;
    }

    public int getStatus_flagging() {
        return status_flagging;
    }

    public void setStatus_flagging(int status_flagging) {
        this.status_flagging = status_flagging;
    }
}
