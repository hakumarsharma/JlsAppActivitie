package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Pojo implementation for forgot password otp.
 */
public class ForgetPassToken {

    @SerializedName("email")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
