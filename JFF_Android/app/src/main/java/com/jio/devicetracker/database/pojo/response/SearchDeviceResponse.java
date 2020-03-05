// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.

package com.jio.devicetracker.database.pojo.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Pojo implementation for search device response .
 */
public class SearchDeviceResponse implements Serializable {

    @SerializedName("code")
    private String code;
    @SerializedName("data")
    private List<SearchDeviceData> mData;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<SearchDeviceData> getmData() {
        return mData;
    }

    public void setmData(List<SearchDeviceData> mData) {
        this.mData = mData;
    }

    public class SearchDeviceData implements Serializable{

        @SerializedName("name")
        private String name;
        @SerializedName("phone")
        private String phoneNumber;
        @SerializedName("imei")
        private String imeiNumber;

        public String getImeiNumber() {
            return imeiNumber;
        }

        public void setImeiNumber(String imeiNumber) {
            this.imeiNumber = imeiNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
