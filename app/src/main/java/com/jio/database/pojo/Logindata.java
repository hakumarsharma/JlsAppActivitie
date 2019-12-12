package com.jio.database.pojo;

import com.google.gson.annotations.SerializedName;

public class Logindata {
    @SerializedName("ugs_token")
    private String ugs_token;

    public String getUgs_token() {
        return ugs_token;
    }

    public void setUgs_token(String ugs_token) {
        this.ugs_token = ugs_token;
    }
}
