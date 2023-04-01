package com.rizal.antrianprinting.models.designer;

import java.util.ArrayList;

public class DesignerResponse {
    boolean status;
    Integer code;
    ArrayList<Designer> data;
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

    public ArrayList<Designer> getData() {
        return data;
    }

    public void setData(ArrayList<Designer> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
