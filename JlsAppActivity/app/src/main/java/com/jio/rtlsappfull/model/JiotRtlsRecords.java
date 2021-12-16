package com.jio.rtlsappfull.model;

public class JiotRtlsRecords {

    private long m_RtlsCellId;
    private double m_rtlsLat;
    private double m_rtlsLng;
    private double m_rtlsAccuracy;
    private String m_recordType;

    public void setM_recordType(String m_recordType) {
        this.m_recordType = m_recordType;
    }


    public String getM_recordType() {
        return m_recordType;
    }


    public void setM_RtlsCellId(int m_RtlsCellId) {
        this.m_RtlsCellId = m_RtlsCellId;
    }

    public void setM_rtlsLat(double m_rtlsLat) {
        this.m_rtlsLat = m_rtlsLat;
    }

    public void setM_rtlsLng(double m_rtlsLng) {
        this.m_rtlsLng = m_rtlsLng;
    }

    public void setM_rtlsAccuracy(double m_rtlsAccuracy) {
        this.m_rtlsAccuracy = m_rtlsAccuracy;
    }

    public long getM_RtlsCellId() {
        return m_RtlsCellId;
    }

    public double getM_rtlsLat() {
        return m_rtlsLat;
    }

    public double getM_rtlsLng() {
        return m_rtlsLng;
    }

    public double getM_rtlsAccuracy() {
        return m_rtlsAccuracy;
    }
}
