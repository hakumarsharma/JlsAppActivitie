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

package com.jio.devicetracker.database.pojo.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Pojo implementation to fetch device location from server .
 */
public class LocationApiResponse {

    @SerializedName("evd")
    List<Event> mEventList;

    public List<Event> getmEventList() {
        return mEventList;
    }

    public void setmEventList(List<Event> mEventList) {
        this.mEventList = mEventList;
    }

    public class Event
    {
        @SerializedName("imi")
        private String imei;
        @SerializedName("evt")
        private String event;
        @SerializedName("alc")
        private String alert;
        @SerializedName("dvt")
        private String device;
        @SerializedName("lat")
        private String latitute;
        @SerializedName("lon")
        private String longnitute;
        @SerializedName("spd")
        private String speed;
        @SerializedName("tms")
        private String timeStamp;
        @SerializedName("mob")
        private String mobileNumber;
        @SerializedName("did")
        private String deviceId;

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getAlert() {
            return alert;
        }

        public void setAlert(String alert) {
            this.alert = alert;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public String getLatitute() {
            return latitute;
        }

        public void setLatitute(String latitute) {
            this.latitute = latitute;
        }

        public String getLongnitute() {
            return longnitute;
        }

        public void setLongnitute(String longnitute) {
            this.longnitute = longnitute;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }
    }
}
