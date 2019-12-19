// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

public class Userdata {

    @SerializedName("email")
    private String emailId;
    @SerializedName("password")
    private String password;
    @SerializedName("type")
    private String type;

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
