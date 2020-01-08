// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.database.pojo.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogindetailResponse implements Serializable {
    @SerializedName("ugs_token")
    private String ugs_token;

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    @SerializedName("ugs_token_expiry")
    private Long ugs_token_expiry;

    public String getUgs_token() {
        return ugs_token;
    }

    public void setUgs_token(String ugs_token) {
        this.ugs_token = ugs_token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUgs_token_expiry() {
        return ugs_token_expiry;
    }

    public void setUgs_token_expiry(Long ugs_token_expiry) {
        this.ugs_token_expiry = ugs_token_expiry;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class User implements Serializable {
        @SerializedName("_id")
        private String _id;

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

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }
}
