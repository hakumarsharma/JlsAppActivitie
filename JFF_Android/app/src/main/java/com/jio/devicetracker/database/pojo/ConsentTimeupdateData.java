// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.database.pojo;

/**
 * Pojo implementation for consent time update  data .
 */
public class ConsentTimeupdateData {

    private String consentTime;
    private String consentStatus;
    private String phoneNumber;

    public String getConsentTime() {
        return consentTime;
    }

    public void setConsentTime(String consentTime) {
        this.consentTime = consentTime;
    }

    public String getConsentStatus() {
        return consentStatus;
    }

    public void setConsentStatus(String consentStatus) {
        this.consentStatus = consentStatus;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
