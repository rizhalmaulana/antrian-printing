package com.rizal.antrianprinting.models.antrian;

public class AntrianResponses {
    boolean status;
    Integer code;
    Antrian data;
    AntrianFlagging time;
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

    public Antrian getData() {
        return data;
    }

    public void setData(Antrian data) {
        this.data = data;
    }

    public AntrianFlagging getTime() {
        return time;
    }

    public void setTime(AntrianFlagging time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
