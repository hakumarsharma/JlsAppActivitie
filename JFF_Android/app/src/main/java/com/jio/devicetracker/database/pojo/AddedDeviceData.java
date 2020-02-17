// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.database.pojo;

import java.io.Serializable;

/**
 * Pojo implementation for add device data .
 */
public class AddedDeviceData implements Serializable {

    private String phoneNumber;
    private String relation;
    private String name;
    private String imeiNumber;
    private String lat;
    private String lng;
    private String consentStaus;
    private String consentTime;
    private int consentApprovalTime;
    private boolean isSelected;


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getConsentStaus() {
        return consentStaus;
    }

    public void setConsentStaus(String consentStaus) {
        this.consentStaus = consentStaus;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getConsentTime() {
        return consentTime;
    }

    public void setConsentTime(String consentTime) {
        this.consentTime = consentTime;
    }

    public int getConsentApprovalTime() {
        return consentApprovalTime;
    }

    public void setConsentApprovalTime(int consentApprovalTime) {
        this.consentApprovalTime = consentApprovalTime;
    }
}
