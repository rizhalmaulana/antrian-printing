package com.rizal.antrianprinting.models.antrian;

import java.util.ArrayList;

public class AntrianResponses {
    boolean status;
    Integer code;
    Object data;
    String message;
    ArrayList<TimeScheduleItem> time_schedule;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<TimeScheduleItem> getTime_schedule() {
        return time_schedule;
    }

    public void setTime_schedule(ArrayList<TimeScheduleItem> time_schedule) {
        this.time_schedule = time_schedule;
    }
}
