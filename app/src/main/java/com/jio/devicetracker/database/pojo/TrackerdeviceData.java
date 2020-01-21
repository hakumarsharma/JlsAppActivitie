// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Pojo implementation for device location data.
 */
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
        @SerializedName("currentDate")
        private Long currentDate;

        public Long getCurrentDate() {
            return currentDate;
        }

        public void setCurrentDate(Long currentDate) {
            this.currentDate = currentDate;
        }
    }
}
