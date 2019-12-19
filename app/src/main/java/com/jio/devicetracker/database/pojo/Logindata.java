// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

public class Logindata {
    @SerializedName("ugs_token")
    private String ugsToken;

    public String getUgsToken() {
        return ugsToken;
    }

    public void setUgsToken(String ugsToken) {
        this.ugsToken = ugsToken;
    }
}
