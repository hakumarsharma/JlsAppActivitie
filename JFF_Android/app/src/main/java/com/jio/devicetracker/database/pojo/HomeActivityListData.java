/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.

 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.

 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.database.pojo;

public class HomeActivityListData {

    private String name;
    private String number;
    private boolean isGroupMember;
    private String phoneNumber;
    private String relation;
    private String imeiNumber;
    private String lat;
    private String lng;
    private String consentStaus;
    private String consentTime;
    private int consentApprovalTime;
    private boolean isSelected;
    private String relationWithName;
    private String deviceType;
    private String groupName;
    public int isCreated;

    public int getIsCreated() {
        return isCreated;
    }

    public void setIsCreated(int isCreated) {
        this.isCreated = isCreated;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNumber() {
        return number;
    }

    public boolean isGroupMember() {
        return isGroupMember;
    }

    public void setGroupMember(boolean groupMember) {
        isGroupMember = groupMember;
    }

    public void setNumber(String number) {
        this.number = number;
    }

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

    public String getRelationWithName() {
        return relationWithName;
    }

    public void setRelationWithName(String relationWithName) {
        this.relationWithName = relationWithName;
    }

}
