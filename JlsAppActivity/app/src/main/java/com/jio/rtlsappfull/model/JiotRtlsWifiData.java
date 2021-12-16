package com.jio.rtlsappfull.model;

public class JiotRtlsWifiData {
    public String getM_BSSID() {
        return m_BSSID;
    }

    public String getM_SSID() {
        return m_SSID;
    }

    public int getM_level() {
        return m_level;
    }

    public int getM_frequency() {
        return m_frequency;
    }

    public int getM_channelWidth() {
        return m_channelWidth;
    }

    public void setM_BSSID(String m_BSSID) {
        this.m_BSSID = m_BSSID;
    }

    public void setM_SSID(String m_SSID) {
        this.m_SSID = m_SSID;
    }

    public void setM_level(int m_level) {
        this.m_level = m_level;
    }

    public void setM_frequency(int m_frequency) {
        this.m_frequency = m_frequency;
    }

    public void setM_channelWidth(int m_channelWidth) {
        this.m_channelWidth = m_channelWidth;
    }

    public String m_BSSID;
    public String m_SSID;
    public int m_level;
    public int m_frequency;
    public int m_channelWidth;
}
