package com.rizal.antrianprinting.models.waktuselesai;

import com.rizal.antrianprinting.models.waktubooking.WaktuBooking;

import java.util.ArrayList;

public class WaktuSelesaiResponse {
    boolean status;
    Integer code;
    ArrayList<WaktuSelesai> data;
    String message;

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

    public ArrayList<WaktuSelesai> getData() {
        return data;
    }

    public void setData(ArrayList<WaktuSelesai> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
