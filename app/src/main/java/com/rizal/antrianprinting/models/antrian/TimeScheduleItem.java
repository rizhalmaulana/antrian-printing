package com.rizal.antrianprinting.models.antrian;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimeScheduleItem {

    public TimeScheduleItem() {
    }

    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("time")
    private int time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
