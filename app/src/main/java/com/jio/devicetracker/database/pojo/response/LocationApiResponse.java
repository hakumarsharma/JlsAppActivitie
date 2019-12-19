// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.database.pojo.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
