package com.example.nutapp;

public class BleDetails {
    String deviceName;
    String deviceAddress;

    public String getDeviceAddress() {
        return deviceAddress;
    }

    String device_rssi;
    int buzz_Photo_id;
    int connect_photo_id;
    double m_distance;

    public BleDetails(String deviceName, String deviceAddress, String device_rssi, int buzz_Photo_id, int connect_photo_id, double distance) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.device_rssi = device_rssi;
        this.buzz_Photo_id = buzz_Photo_id;
        this.connect_photo_id = connect_photo_id;
        this.m_distance = distance;
    }
}
