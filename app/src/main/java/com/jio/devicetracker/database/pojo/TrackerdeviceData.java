// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TrackerdeviceData implements Serializable {

    @SerializedName("startsWith")
    private StartsWith startsWith;

    public StartsWith getStartsWith() {
        return startsWith;
    }

    public void setStartsWith(StartsWith startsWith) {
        this.startsWith = startsWith;
    }

    public class StartsWith implements Serializable {
        @SerializedName("currentDat")
        private String currentDat;

        public String getCurrentDat() {
            return currentDat;
        }

        public void setCurrentDat(String currentDat) {
            this.currentDat = currentDat;
        }
    }
}
