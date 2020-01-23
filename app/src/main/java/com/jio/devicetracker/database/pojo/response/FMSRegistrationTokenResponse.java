// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.database.pojo.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FMSRegistrationTokenResponse implements Serializable {
    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private Data data;
    @SerializedName("status")
    private String status;
    @SerializedName("type")
    private String type;
    @SerializedName("email")
    private String email;
    @SerializedName("userDetails")
    private String userDetails;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(String userDetails) {
        this.userDetails = userDetails;
    }

    public class Data implements Serializable {
        @SerializedName("_id")
        private String id;
        @SerializedName("authType")
        private String authType;
        @SerializedName("registration")
        private Registration registration;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAuthType() {
            return authType;
        }

        public void setAuthType(String authType) {
            this.authType = authType;
        }

        public Registration getRegistration() {
            return registration;
        }

        public void setRegistration(Registration registration) {
            this.registration = registration;
        }
    }

    public class Registration implements Serializable{
        @SerializedName("count")
        private int count;
        @SerializedName("autoUnlock")
        private boolean autoUnlock;
        @SerializedName("_id")
        private String id;
        @SerializedName("createdAt")
        private String createdAt;
        @SerializedName("token")
        private String token;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public boolean isAutoUnlock() {
            return autoUnlock;
        }

        public void setAutoUnlock(boolean autoUnlock) {
            this.autoUnlock = autoUnlock;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
