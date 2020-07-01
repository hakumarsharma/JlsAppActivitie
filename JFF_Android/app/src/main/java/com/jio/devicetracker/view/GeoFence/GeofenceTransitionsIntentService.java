package com.jio.devicetracker.view.geofence;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.android.gms.location.Geofence;

public class GeofenceTransitionsIntentService extends IntentService {
    int geofenceTransition;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // do something
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // do something else
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            // do something else again
        }
    }
}
