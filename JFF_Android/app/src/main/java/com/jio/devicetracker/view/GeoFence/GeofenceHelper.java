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

package com.jio.devicetracker.view.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceHelper extends ContextWrapper {

    PendingIntent pendingIntent;

    public GeofenceHelper(Context base) {
        super(base);
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence){

        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .build();
    }
    public Geofence getGeofence(String id, LatLng latlng, float radius , int transitionType){

        return new Geofence.Builder()
                .setCircularRegion(latlng.latitude, latlng.longitude, radius)
                .setRequestId(id)
                .setTransitionTypes(transitionType)
                .setLoiteringDelay(3000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    public  PendingIntent getPendingIntent(){
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return  pendingIntent;
    }
    public String getErrorString(Exception e) {
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()) {
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
                default:
                    break;
            }
        }
        return e.getLocalizedMessage();
    }
}
