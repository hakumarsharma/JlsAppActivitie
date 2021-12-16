package com.jio.rtlsappfull.model;

public class JiotRtlsBleRecord {
    public String m_macAddress;
    public String m_deviceName;
    public long m_age;
    public long m_rssi;

    public void setM_macAddress(String m_macAddress) {
        this.m_macAddress = m_macAddress;
    }

    public void setM_deviceName(String m_deviceName) {
        this.m_deviceName = m_deviceName;
    }

    public void setM_age(long m_age) {
        this.m_age = m_age;
    }

    public void setM_rssi(long m_rssi) {
        this.m_rssi = m_rssi;
    }

    public String getM_macAddress() {
        return m_macAddress;
    }

    public String getM_deviceName() {
        return m_deviceName;
    }

    public long getM_age() {
        return m_age;
    }

    public long getM_rssi() {
        return m_rssi;
    }
}
