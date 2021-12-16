package com.jio.rtlsappfull.model;

public class JiotRtlsLocationRecord {
    public double m_latitude;
    public double m_longitude;
    public double m_accuracy;
    public long m_age;
    public double m_altitude;
    public double m_altitudeAccuracy;
    public double m_heading;
    public double m_pressure;
    public double m_speed;
    public String m_source;

    public void setM_latitude(double m_latitude) {
        this.m_latitude = m_latitude;
    }

    public void setM_longitude(double m_longitude) {
        this.m_longitude = m_longitude;
    }

    public void setM_accuracy(double m_accuracy) {
        this.m_accuracy = m_accuracy;
    }

    public void setM_age(long m_age) {
        this.m_age = m_age;
    }

    public void setM_altitude(double m_altitude) {
        this.m_altitude = m_altitude;
    }

    public void setM_altitudeAccuracy(double m_altitudeAccuracy) {
        this.m_altitudeAccuracy = m_altitudeAccuracy;
    }

    public void setM_heading(double m_heading) {
        this.m_heading = m_heading;
    }

    public void setM_pressure(double m_pressure) {
        this.m_pressure = m_pressure;
    }

    public void setM_speed(double m_speed) {
        this.m_speed = m_speed;
    }

    public void setM_source(String m_source) {
        this.m_source = m_source;
    }

    public double getM_latitude() {
        return m_latitude;
    }

    public double getM_longitude() {
        return m_longitude;
    }

    public double getM_accuracy() {
        return m_accuracy;
    }

    public long getM_age() {
        return m_age;
    }

    public double getM_altitude() {
        return m_altitude;
    }

    public double getM_altitudeAccuracy() {
        return m_altitudeAccuracy;
    }

    public double getM_heading() {
        return m_heading;
    }

    public double getM_pressure() {
        return m_pressure;
    }

    public double getM_speed() {
        return m_speed;
    }

    public String getM_source() {
        return m_source;
    }
}
