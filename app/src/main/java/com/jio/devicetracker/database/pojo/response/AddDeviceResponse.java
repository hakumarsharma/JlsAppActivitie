// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.database.pojo.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AddDeviceResponse implements Serializable {
    @SerializedName("code")
    private String code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

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

    public class Data implements Serializable {
        @SerializedName("assignedDevices")
        private List<AssignedDevices> assignedDevices;

        public List<AssignedDevices> getAssignedDevices() {
            return assignedDevices;
        }

        public void setAssignedDevices(List<AssignedDevices> assignedDevices) {
            this.assignedDevices = assignedDevices;
        }

        public class AssignedDevices implements Serializable {
            @SerializedName("mac")
            private String mac;
            @SerializedName("name")
            private String name;
            @SerializedName("phone")
            private String phone;
            @SerializedName("_id")
            private String id;

            public String getMac() {
                return mac;
            }

            public void setMac(String mac) {
                this.mac = mac;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }
}
