/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.

 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.

 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.database.pojo.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Pojo implementation to fetch login response from server .
 */
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

        @SerializedName("phoneCountryCode")
        private String phoneCountryCode;

        public String getPhoneCountryCode() {
            return phoneCountryCode;
        }

        public void setPhoneCountryCode(String phoneCountryCode) {
            this.phoneCountryCode = phoneCountryCode;
        }

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
