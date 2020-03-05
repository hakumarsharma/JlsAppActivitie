// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.database.pojo.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TrackerdeviceResponse implements Serializable {
    @SerializedName("code")
    private String code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<Data> mData;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Data> getmData() {
        return mData;
    }

    public void setmData(List<Data> mData) {
        this.mData = mData;
    }

    public class Data implements Serializable{

        @SerializedName("device")
        private Device mDevice;

        @SerializedName("_id")
        private String id;

        @SerializedName("latestEvents")
        private LatestEvent event;

        @SerializedName("location")
        private Location location;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Device getmDevice() {
            return mDevice;
        }

        public void setmDevice(Device mDevice) {
            this.mDevice = mDevice;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public LatestEvent getEvent() {
            return event;
        }

        public void setEvent(LatestEvent event) {
            this.event = event;
        }
    }

    public class Location implements Serializable{

        @SerializedName("lat")
        private Double lat;

        @SerializedName("lng")
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }
    }

    public class Device implements Serializable
    {
        @SerializedName("phone")
        private String phoneNumber;
        @SerializedName("imei")
        private String imeiNumber;

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getImeiNumber() {
            return imeiNumber;
        }

        public void setImeiNumber(String imeiNumber) {
            this.imeiNumber = imeiNumber;
        }
    }

    public class LatestEvent implements Serializable
    {
        @SerializedName("location")
        private Locationdata location;

        public Locationdata getLocation() {
            return location;
        }

        public void setLocation(Locationdata location) {
            this.location = location;
        }
    }

    public class Locationdata implements Serializable
    {
        @SerializedName("location")
        private Latestocation latLocation;

        public Latestocation getLatLocation() {
            return latLocation;
        }

        public void setLatLocation(Latestocation latLocation) {
            this.latLocation = latLocation;
        }
    }
    public class Latestocation implements Serializable
    {
        @SerializedName("lng")
        private String longni;
        @SerializedName("lat")
        private String latitu;

        public String getLongni() {
            return longni;
        }

        public void setLongni(String longni) {
            this.longni = longni;
        }

        public String getLatitu() {
            return latitu;
        }

        public void setLatitu(String latitu) {
            this.latitu = latitu;
        }
    }
}
