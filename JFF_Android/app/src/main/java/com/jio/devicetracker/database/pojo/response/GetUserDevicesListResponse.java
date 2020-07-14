package com.jio.devicetracker.database.pojo.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetUserDevicesListResponse implements Serializable {
    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<GetUserDevicesListResponse.Data> data;

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

    public List<GetUserDevicesListResponse.Data> getData() {
        return data;
    }

    public void setData(List<GetUserDevicesListResponse.Data> data) {
        this.data = data;
    }

    public class Data implements Serializable{
        @SerializedName("_id")
        private String id;
        @SerializedName("device")
        private Devices devices;

        public Devices getDevices() {
            return devices;
        }

        public void setDevices(Devices devices) {
            this.devices = devices;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    public class Devices implements  Serializable {
        @SerializedName("_id")
        private String deviceId;
        @SerializedName("name")
        private String name;
        @SerializedName("phone")
        private String phone;
        @SerializedName("identifier")
        private String identifier;
        @SerializedName("mac")
        private String mac;
        @SerializedName("imei")
        private String imei;
        @SerializedName("deviceId")
        private String userDeviceId;
        @SerializedName("type")
        private String type;
        @SerializedName("model")
        private String model;
        @SerializedName("user")
        private String user;
        @SerializedName("devicestatus")
        private String devicestatus;
        @SerializedName("deviceSetting")
        private String deviceSetting;

        public String getDeviceId() { return deviceId; }

        public void setDeviceId(String userId) {
            this.deviceId = deviceId;
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

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getMac() {return  mac;}
        public void setMac(String mac){this.mac = mac;}

        public String getImei() {return imei;}
        public void setImei(String imei){this.imei = imei;}

        public String getUserDeviceId() {return userDeviceId;}
        public String setUserDeviceId() {return this.userDeviceId = userDeviceId;}

        public String getType(){return type;}
        public String setType() {return this.type = type;}

        public String getModel(){return model;}
        public String setModel() {return this.model = model;}

        public String getUser(){return user;}
        public String setUser() {return this.user = user;}

        public String getDevicestatus(){return devicestatus;}
        public String setDevicestatus() {return this.devicestatus = devicestatus;}

        public String getDeviceSetting(){return deviceSetting;}
        public String setDeviceSetting() {return this.deviceSetting = deviceSetting;}



    }
}
