package com.rizal.antrianprinting.models.layanan;

import com.rizal.antrianprinting.models.designer.Designer;

import java.util.ArrayList;

public class LayananResponse {
    boolean status;
    Integer code;
    ArrayList<Layanan> data;
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

    public ArrayList<Layanan> getData() {
        return data;
    }

    public void setData(ArrayList<Layanan> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
