package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SearchDeviceStatusData implements Serializable {
    @SerializedName("device")
    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public class Device implements Serializable {
        @SerializedName("usersAssigned")
        private List<String> usersAssigned;

        public List<String> getUsersAssigned() {
            return usersAssigned;
        }

        public void setUsersAssigned(List<String> usersAssigned) {
            this.usersAssigned = usersAssigned;
        }

    }
}
