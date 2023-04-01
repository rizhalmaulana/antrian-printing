package com.rizal.antrianprinting.models.waktubooking;

import java.util.ArrayList;
import java.util.List;

public class WaktuBookingResponse {
    boolean status;
    Integer code;
    ArrayList<WaktuBooking> data;
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

    public ArrayList<WaktuBooking> getData() {
        return data;
    }

    public void setData(ArrayList<WaktuBooking> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
