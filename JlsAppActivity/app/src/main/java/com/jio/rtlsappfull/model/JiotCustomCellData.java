package com.jio.rtlsappfull.model;

public class JiotCustomCellData {
    public String m_radioType;
    public int m_mobileCountryCode;
    public int m_mobileNetworkCode;
    public int m_locationAreaCode;
    public long m_cellId;
    public int m_psc;
    public int m_signalStrength;
    public boolean m_isPrimary;
    public int m_timingAdvance;
    public int m_frequency;

    public void setM_timingAdvance(int m_timingAdvance) {
        this.m_timingAdvance = m_timingAdvance;
    }

    public int getM_timingAdvance() {
        return m_timingAdvance;
    }

    public void setM_latitude(double m_latitude) {
        this.m_latitude = m_latitude;
    }

    public void setM_longitude(double m_longitude) {
        this.m_longitude = m_longitude;
    }

    public double getM_latitude() {
        return m_latitude;
    }

    public double getM_longitude() {
        return m_longitude;
    }

    public int getFrequency() {
        return m_frequency;
    }

    public void setM_frequency(int frequency) {
        m_frequency = frequency;
    }

    public double m_latitude;
    public double m_longitude;

    public void setM_radioType(String m_radioType) {
        this.m_radioType = m_radioType;
    }

    public void setM_mobileCountryCode(int m_mobileCountryCode) {
        this.m_mobileCountryCode = m_mobileCountryCode;
    }

    public void setM_mobileNetworkCode(int m_mobileNetworkCode) {
        this.m_mobileNetworkCode = m_mobileNetworkCode;
    }

    public void setM_locationAreaCode(int m_locationAreaCode) {
        this.m_locationAreaCode = m_locationAreaCode;
    }

    public void setM_cellId(long m_cellId) {
        this.m_cellId = m_cellId;
    }

    public void setM_psc(int m_psc) {
        this.m_psc = m_psc;
    }

    public void setM_signalStrength(int m_signalStrength) {
        this.m_signalStrength = m_signalStrength;
    }

    public void setM_isPrimary(boolean m_isPrimary) {
        this.m_isPrimary = m_isPrimary;
    }

    public String getM_radioType() {
        return m_radioType;
    }

    public int getM_mobileCountryCode() {
        return m_mobileCountryCode;
    }

    public int getM_mobileNetworkCode() {
        return m_mobileNetworkCode;
    }

    public int getM_locationAreaCode() {
        return m_locationAreaCode;
    }

    public long getM_cellId() {
        return m_cellId;
    }

    public int getM_psc() {
        return m_psc;
    }

    public int getM_signalStrength() {
        return m_signalStrength;
    }

    public boolean isM_isPrimary() {
        return m_isPrimary;
    }

}
