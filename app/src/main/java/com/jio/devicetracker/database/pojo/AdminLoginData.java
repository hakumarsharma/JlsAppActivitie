// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.database.pojo;

/**
 * Pojo implementation for admin login data .
 */
public class AdminLoginData {

    private String email;
    private String userToken;
    private String tokenExpirytime;
    private String userId;
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

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getTokenExpirytime() {
        return tokenExpirytime;
    }

    public void setTokenExpirytime(String tokenExpirytime) {
        this.tokenExpirytime = tokenExpirytime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
