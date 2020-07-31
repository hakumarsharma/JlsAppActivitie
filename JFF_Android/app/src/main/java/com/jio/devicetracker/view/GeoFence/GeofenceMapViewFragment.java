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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GeofenceDetails;
import com.jio.devicetracker.util.Constant;
import java.util.List;


public class GeofenceMapViewFragment extends Fragment implements OnMapReadyCallback {

    public static GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_MAPS = 101;
    public Toolbar toolbar;
    List<GeofenceDetails> geofenceDetail;
    private DBManager mDbManager;
    private String deviceNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        deviceNumber = getActivity().getIntent().getStringExtra(Constant.DEVICE_NUMBER);
        mDbManager = new DBManager(getActivity());
        geofenceDetail = mDbManager.getGeofenceDetailsList(deviceNumber);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.locationOnMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_MAPS);
        }


        return view;
    }


    /**
     * Call back method to load the map on screen
     * Show the alerts if group is not active or it is completed
     *
     * @param googleMap
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        if (geofenceDetail != null && !geofenceDetail.isEmpty()) {
            for(GeofenceDetails details : geofenceDetail) {
                LatLng latlng = new LatLng(details.getLat(),details.getLng());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                addCircle(latlng,details.getRadius());
            }
        }
    }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void addCircle (LatLng latLng,int geofenceRadiusInMeters){
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.radius(geofenceRadiusInMeters);
            circleOptions.strokeColor(getActivity().getResources().getColor(R.color.geofence_background_fill_color));
            circleOptions.fillColor(getActivity().getResources().getColor(R.color.geofence_background_fill_color));
            circleOptions.strokeWidth(4);
            mMap.addCircle(circleOptions);
        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        geofenceDetail = mDbManager.getGeofenceDetailsList(deviceNumber);
        if (mMap != null) {
            onMapReady(mMap);
        }
    }
}

