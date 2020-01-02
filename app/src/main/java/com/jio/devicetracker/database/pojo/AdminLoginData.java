package com.jio.devicetracker.database.pojo;

public class AdminLoginData {

    private String email;
    private String user_token;
    private String token_expirtime;
    private String userId;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public String getToken_expirtime() {
        return token_expirtime;
    }

    public void setToken_expirtime(String token_expirtime) {
        this.token_expirtime = token_expirtime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
