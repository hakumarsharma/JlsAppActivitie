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

/**
 * This class is using to handle the getLocation API response and converting the response in pojo class.
 */

package com.jio.devicetracker.database.pojo.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetDeviceLocationResponse implements Serializable {

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data implements Serializable{

        @SerializedName("phone")
        private String phoneNumber;

        @SerializedName("devicestatus")
        private DeviceStatus deviceStatus;

      /*  public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }
*/
        public DeviceStatus getDeviceStatus() {
            return deviceStatus;
        }

        public void setDeviceStatus(DeviceStatus deviceStatus) {
            this.deviceStatus = deviceStatus;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }

    public class DeviceStatus implements Serializable{
        @SerializedName("location")
        private LocationDetail location;

        public LocationDetail getLocation() {
            return location;
        }

        public void setLocation(LocationDetail location) {
            this.location = location;
        }
    }

    public class LocationDetail implements Serializable{
        @SerializedName("lat")
        private double lat;
        @SerializedName("lng")
        private double lng;


        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

}
