package com.rizal.antrianprinting.models.designer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Designer {

    public Designer() {
    }

    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("username")
    private String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
