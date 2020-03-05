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
