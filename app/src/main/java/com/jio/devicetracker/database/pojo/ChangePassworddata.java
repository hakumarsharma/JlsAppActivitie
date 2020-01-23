package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Pojo implementation for change password  data .
 */
public class ChangePassworddata {

    @SerializedName("token")
    private EmailToken mToken;
    @SerializedName("email")
    private String emialId;
    @SerializedName("password")
    private String newPass;

    public EmailToken getmToken() {
        return mToken;
    }

    public void setmToken(EmailToken mToken) {
        this.mToken = mToken;
    }

    public String getEmialId() {
        return emialId;
    }

    public void setEmialId(String emialId) {
        this.emialId = emialId;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public class EmailToken
    {
        @SerializedName("value")
        private String tokenValue;

        public String getTokenValue() {
            return tokenValue;
        }

        public void setTokenValue(String tokenValue) {
            this.tokenValue = tokenValue;
        }
    }

}
