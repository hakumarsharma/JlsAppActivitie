// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.database.pojo.response;

import com.google.gson.annotations.SerializedName;
import com.jio.database.pojo.Logindata;

import java.io.Serializable;

public class LogindetailResponse implements Serializable {
    @SerializedName("data")
    private Logindata logindata;

    @SerializedName("message")
    private String message;

    public Logindata getLogindata() {
        return logindata;
    }

    public void setLogindata(Logindata logindata) {
        this.logindata = logindata;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
