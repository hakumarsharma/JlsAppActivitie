/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.

 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.

 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/


package com.jio.devicetracker.database.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class MapData implements Parcelable {
    private String name;
    private double latitude;
    private double longitude;
    private String consentId;

    protected MapData(Parcel in) {
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        consentId = in.readString();
    }

    public MapData () {
        // Empty constructor
    }

    public static final Creator<MapData> CREATOR = new Creator<MapData>() {
        @Override
        public MapData createFromParcel(Parcel in) {
            return new MapData(in);
        }

        @Override
        public MapData[] newArray(int size) {
            return new MapData[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(consentId);
    }
}
