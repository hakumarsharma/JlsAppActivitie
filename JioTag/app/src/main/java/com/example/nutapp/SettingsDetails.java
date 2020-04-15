package com.example.nutapp;

public class SettingsDetails {
    String m_settinsMain;
    String m_settingsDetails;

    public void setM_settinsMain(String m_settinsMain) {
        this.m_settinsMain = m_settinsMain;
    }

    public void setM_settingsDetails(String m_settingsDetails) {
        this.m_settingsDetails = m_settingsDetails;
    }

    public String getM_settinsMain() {
        return m_settinsMain;
    }

    public String getM_settingsDetails() {
        return m_settingsDetails;
    }

    public SettingsDetails(String m_settinsMain, String m_settingsDetails) {
        this.m_settinsMain = m_settinsMain;
        this.m_settingsDetails = m_settingsDetails;
    }
}
