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
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GeofenceDetails;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.location.MapsActivity;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class GeofenceMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    public static GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_MAPS = 101;
    public LocationManager locationManager;
    public static String TAG = "GeofenceMapFragment";
    public Toolbar toolbar;
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private static Context context = null;
    //private List<MapData> mapDataList;
    private LatLng latLng;
    private DBManager mDbManager;
    private GeofenceHelper geofenceHelper;
    private int GEOFENCE_RADIUS_IN_METERS = 200;
    double Longitude =73.76976049999999;
    double Latitude = 19.9756696;
    private GeofencingClient mGeofencingClient;
    private String GEOFENCE_ID = "JioTrack1";
    private LatLng trackeeLatlng;
    private LatLng geoFenceLatlng;
    private boolean createGeofence;
    private boolean geoFenceEntryExit;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1000;
    private int editGeofenceRadius = 0;
    private List<MapData> mapDataList;
    private NotificationHelper notificationHelper;
    private String deviceNumber;
    private double lat;
    private double lang;
    private GeofenceDetails geofenceDetails;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        notificationHelper = new NotificationHelper(getActivity());
        if (getArguments() != null) {
            lat = getArguments().getDouble(Constant.LATITUDE);
            lang = getArguments().getDouble(Constant.LONGNITUDE);
            editGeofenceRadius = getArguments().getInt(Constant.GEOFENCE_RADIUS);
            if (!(editGeofenceRadius == 0)) {
                GEOFENCE_RADIUS_IN_METERS = editGeofenceRadius;
            }

            createGeofence = getArguments().getBoolean(Constant.CREATE_GEOFENCE);
            /*if (lat != 0 && lang != 0) {
                geoFenceLatlng = new LatLng(lat
                        , lang);

            }*/
        }
        context = getContext();
        mapDataList = getActivity().getIntent().getParcelableArrayListExtra(Constant.MAP_DATA);
        deviceNumber = getActivity().getIntent().getStringExtra(Constant.DEVICE_NUMBER);
        mDbManager = new DBManager(getActivity());
        geofenceDetails = mDbManager.getGeofenceDetails(deviceNumber);
        //mapDataList = getActivity().getIntent().getParcelableArrayListExtra(Constant.MAP_DATA);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.locationOnMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        MapsInitializer.initialize(getContext());
        mGeofencingClient = LocationServices.getGeofencingClient(getActivity());
        geofenceHelper = new GeofenceHelper(getActivity());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_MAPS);
        }

        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, Constant.PERMISSION_GRANTED);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

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
        //if (mapDataList.isEmpty()) {
        MarkerOptions markerOptions = new MarkerOptions();
        if (!(geofenceDetails.getLat() == 0) && !(geofenceDetails.getLng() == 0) && !createGeofence) {
            geoFenceLatlng = new LatLng(geofenceDetails.getLat(), geofenceDetails.getLng());
        } else if(createGeofence) {
            geoFenceLatlng = new LatLng(lat, lang);
        } else {
            geoFenceLatlng = new LatLng(Latitude, Longitude);
        }
        markerOptions.position(geoFenceLatlng);
        mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(geoFenceLatlng, 14));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        addCircle(geoFenceLatlng, GEOFENCE_RADIUS_IN_METERS);
        addGeofence(geoFenceLatlng, GEOFENCE_RADIUS_IN_METERS);

        if (mapDataList != null && !mapDataList.isEmpty()) {
            trackeeLatlng = new LatLng(mapDataList.get(0).getLatitude(), mapDataList.get(0).getLongitude());
            addMarker(trackeeLatlng);
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addCircle(LatLng latLng, int geofenceRadiusInMeters) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(geofenceRadiusInMeters);
        circleOptions.strokeColor(getResources().getColor(R.color.geofence_background_fill_color));
        circleOptions.fillColor(getResources().getColor(R.color.geofence_background_fill_color));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapClick(LatLng latLng) {

        //addMarker(latLng);
        //handleMapClick(latLng);

    }

    /**
     * Add the details in Geofence.
     */
    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        mGeofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d(TAG, Constant.GEOFENCE_SUCCESS_MESSAGE);
                        mDbManager.updateGeofenceDetailInGroupMemberTable(geoFenceLatlng, GEOFENCE_RADIUS_IN_METERS, deviceNumber);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, Constant.GEOFENCE_FAILURE_MESSAGE + errorMessage);
                    }
                });
    }


    /*@RequiresApi(api = Build.VERSION_CODES.O)
    private void handleMapClick(LatLng latLng) {
        mMap.clear();
        addMarker(latLng);
        addCircle(latLng, GEOFENCE_RADIUS_IN_METERS);
        addGeofence(latLng, GEOFENCE_RADIUS_IN_METERS);
        float distance = distance((float) latLng.latitude, (float) latLng.longitude, (float) Latitude, (float) Longitude);
        int radiusDifference = (int) distance;
        trackGeofenceTransition(radiusDifference);
    }*/

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

  /*  *//**
     * Fetch trackee location after every 30 seconds
     *//*
    public class FetchLocation implements Runnable {
        public void run() {
            while (true) {
                try {
                    makeMQTTConnection();
                    Thread.sleep(30000);
                    publishMessage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, Constant.PERMISSION_GRANTED);
            } else {
                Log.d(TAG, Constant.PERMISSION_NOT_GRANTED);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapLongClick(LatLng latLng) {
        //handleMapClick(latLng);
    }




  /* public void makeApicallForTrackeeLocation(){
       SearchEventData searchEventData = new SearchEventData();
       List<String> mList = new ArrayList<>();
       mList.add(Constant.LOCATION);
       mList.add(Constant.SOS);
       searchEventData.setTypes(mList);
       Util.getInstance().showProgressBarDialog(getActivity());
       GroupRequestHandler.getInstance(getContext()).handleRequest(new SearchEventRequest(new SearchEventRequestSuccessListener(), new SearchEventRequestErrorListener(), searchEventData, mDbManager.getAdminLoginDetail().getUserId(), homeActivityListData.getGroupId(), Constant.GET_LOCATION_URL));
    }

    *//**
     * Search Event Request API call Success Listener
     *//*
    private class SearchEventRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            SearchEventResponse searchEventResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchEventResponse.class);
            List<MapData> mapDataList = new ArrayList<>();
            List<SearchEventResponse.Data> mList = searchEventResponse.getData();
            if (!mList.isEmpty()) {
                List<GroupMemberDataList> grpMembersOfParticularGroupId = mDbManager.getAllGroupMemberDataBasedOnGroupId(groupId);
                for (SearchEventResponse.Data data : mList) {
                    for (GroupMemberDataList grpMembers : grpMembersOfParticularGroupId) {
                        if (grpMembers.getDeviceId() != null && grpMembers.getDeviceId().equalsIgnoreCase(data.getDevice()) && grpMembers.getUserId().equalsIgnoreCase(data.getUserId())) {
                            MapData mapData = new MapData();
                            mapData.setLatitude(data.getLocation().getLat());
                            mapData.setLongitude(data.getLocation().getLng());
                            mapData.setName(grpMembers.getName());
                            mapData.setConsentId(grpMembers.getConsentId());
                            mapDataList.add(mapData);
                        }
                    }
                }
            }
            //goToMapActivity(mapDataList);
        }
    }

    */

    /**
     * Search Event Request API Call Error listener
     *//*
    private class SearchEventRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
           // showCustomAlertWithText(Constant.FETCH_LOCATION_ERROR);
        }
    }*/
    public float distance(float latA, float lngA, float latB, float lngB) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(latB - latA);
        double lngDiff = Math.toRadians(lngB - lngA);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(latA)) * Math.cos(Math.toRadians(latB)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

    public void trackGeofenceTransition(int distance){
        if(distance < GEOFENCE_RADIUS_IN_METERS){
            geoFenceEntryExit = true;
            notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", MapsActivity.class);
        } else if(distance > GEOFENCE_RADIUS_IN_METERS && geoFenceEntryExit ) {
            geoFenceEntryExit = false;
            notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MapsActivity.class);
        }

    }
}