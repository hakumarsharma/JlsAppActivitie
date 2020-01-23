package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SearchEventData implements Serializable {
    @SerializedName("device")
    private Device device;
    @SerializedName("from")
    private Long from;
    @SerializedName("to")
    private Long to;
    @SerializedName("flags")
    private Flags flags;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public class Device implements Serializable {
        @SerializedName("_id")
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public class Flags implements Serializable {
        @SerializedName("isPopulateGeofence")
        private boolean isPopulateGeofence;
        @SerializedName("isPopulateRoute")
        private boolean isPopulateRoute;

        public boolean isPopulateGeofence() {
            return isPopulateGeofence;
        }

        public void setPopulateGeofence(boolean populateGeofence) {
            isPopulateGeofence = populateGeofence;
        }

        public boolean isPopulateRoute() {
            return isPopulateRoute;
        }

        public void setPopulateRoute(boolean populateRoute) {
            isPopulateRoute = populateRoute;
        }
    }
}
