package com.example.nutapp;

public class BleDetails {
    String deviceName;
    String deviceAddress;

    public String getDeviceAddress() {
        return deviceAddress;
    }

    String deviceRssi;
    int buzzPhotoId;
    int connectPhotoId;
    double mDistance;

    public BleDetails(String deviceName, String deviceAddress, String deviceRssi, int buzzPhotoId, int connectPhotoId, double distance) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.deviceRssi = deviceRssi;
        this.buzzPhotoId = buzzPhotoId;
        this.connectPhotoId = connectPhotoId;
        this.mDistance = distance;
    }
}
