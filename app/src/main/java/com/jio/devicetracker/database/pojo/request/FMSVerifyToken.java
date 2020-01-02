package com.jio.devicetracker.database.pojo.request;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FMSVerifyToken implements Serializable {
    @SerializedName("email")
    private String email;
    @SerializedName("type")
    private String type;
    @SerializedName("token")
    private String token;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
