package com.example.nutapp;

public class SettingsDetails {
    String mSettinsMain;
    String mSettingsDetails;

    public void setmSettinsmain(String mSettinsMain) {
        this.mSettinsMain = mSettinsMain;
    }

    public void setmSettingsdetails(String mSettingsDetails) {
        this.mSettingsDetails = mSettingsDetails;
    }

    public String getmSettinsmain() {
        return mSettinsMain;
    }

    public String getmSettingsdetails() {
        return mSettingsDetails;
    }

    public SettingsDetails(String mSettinsMain, String mSettingsDetails) {
        this.mSettinsMain = mSettinsMain;
        this.mSettingsDetails = mSettingsDetails;
    }
}
