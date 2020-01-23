package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Pojo implementation for register request data for registration api .
 */
public class RegisterRequestData {

    @SerializedName("email")
    private String email;
    @SerializedName("type")
    private String type;
    @SerializedName("phone")
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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
}
