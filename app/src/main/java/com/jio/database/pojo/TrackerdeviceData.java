// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.database.pojo;

import com.google.gson.annotations.SerializedName;

public class TrackerdeviceData {

    @SerializedName("location")
    private Latlong latlong;
    @SerializedName("sort")
    private Sort mSort;

    public Latlong getLatlong() {
        return latlong;
    }

    public void setLatlong(Latlong latlong) {
        this.latlong = latlong;
    }

    public Sort getmSort() {
        return mSort;
    }

    public void setmSort(Sort mSort) {
        this.mSort = mSort;
    }

    public class Sort{
        @SerializedName("latest.location")
        private int latestLocation;

        public int getLatestLocation() {
            return latestLocation;
        }

        public void setLatestLocation(int latestLocation) {
            this.latestLocation = latestLocation;
        }
    }

    public class Latlong{

        @SerializedName("from")
        private long from;
        @SerializedName("to")
        private long to;

        public long getFrom() {
            return from;
        }

        public void setFrom(long from) {
            this.from = from;
        }

        public long getTo() {
            return to;
        }

        public void setTo(long to) {
            this.to = to;
        }
    }
}
