// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.database.pojo.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogindetailResponse implements Serializable {
    @SerializedName("ugs_token")
    private String ugsToken;

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    @SerializedName("ugs_token_expiry")
    private Long ugsTokenExpiry;

    public String getUgsToken() {
        return ugsToken;
    }

    public void setUgsToken(String ugsToken) {
        this.ugsToken = ugsToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUgsTokenExpiry() {
        return ugsTokenExpiry;
    }

    public void setUgsTokenExpiry(Long ugsTokenExpiry) {
        this.ugsTokenExpiry = ugsTokenExpiry;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class User implements Serializable {
        @SerializedName("_id")
        private String id;

        @SerializedName("email")
        private String email;

        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
