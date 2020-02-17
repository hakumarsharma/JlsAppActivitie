// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.

package com.jio.devicetracker.database.pojo.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/************ Response of Search Device Status to get lat and long ************/
public class SearchDeviceStatusResponse implements Serializable {
    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<Data> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data implements Serializable {
        @SerializedName("device")
        private Device device;

        @SerializedName("location")
        private Location location;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Device getDevice() {
            return device;
        }

        public void setDevice(Device device) {
            this.device = device;
        }

        public class Device implements Serializable {
            @SerializedName("_id")
            private String id;
            @SerializedName("name")
            private String name;
            @SerializedName("imei")
            private String imei;
            @SerializedName("phone")
            private String phone;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getImei() {
                return imei;
            }

            public void setImei(String imei) {
                this.imei = imei;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }
        }

        public class Location implements Serializable {
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
    }
}
