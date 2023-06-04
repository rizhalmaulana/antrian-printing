package com.rizal.antrianprinting.models.riwayat;

import java.util.ArrayList;

public class RiwayatResponse {
    boolean status;
    Integer code;
    ArrayList<RiwayatBookingItem> data;
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

    public ArrayList<RiwayatBookingItem> getData() {
        return data;
    }

    public void setData(ArrayList<RiwayatBookingItem> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
