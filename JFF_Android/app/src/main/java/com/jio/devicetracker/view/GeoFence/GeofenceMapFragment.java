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
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AlertHistoryData;
import com.jio.devicetracker.database.pojo.GeofenceDetails;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.database.pojo.SearchEventData;
import com.jio.devicetracker.database.pojo.request.SearchEventRequest;
import com.jio.devicetracker.database.pojo.response.SearchEventResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.menu.NotificationsAlertsActivity;
import com.jio.devicetracker.view.menu.settings.GeofenceSettingsAcivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class GeofenceMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    public static GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_MAPS = 101;
    public LocationManager locationManager;
    public static String TAG = "GeofenceMapFragment";
    public Toolbar toolbar;
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private DBManager mDbManager;
    private GeofenceHelper geofenceHelper;
    private int GEOFENCE_RADIUS_IN_METERS = 5000;
    private GeofencingClient mGeofencingClient;
    private String GEOFENCE_ID = "JioTrack1";
    private LatLng trackeeLatlng;
    private LatLng geoFenceLatlng;
    private static boolean geoFenceEntryExit;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1000;
    private List<MapData> mapDataList;
    private String memberName;
    private String deviceNumber;
    private String groupId;
    private double lat;
    private double lang;
    List<GeofenceDetails> geofenceDetail;
    private AlertHistoryData alertHistoryData;
    public static String consentId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        groupId = getActivity().getIntent().getStringExtra(Constant.GROUP_ID);
        mapDataList = getActivity().getIntent().getParcelableArrayListExtra(Constant.MAP_DATA);
        deviceNumber = getActivity().getIntent().getStringExtra(Constant.DEVICE_NUMBER);
        memberName = getActivity().getIntent().getStringExtra(Constant.MEMBER_NAME);
        mDbManager = new DBManager(getActivity());
        /*List<GeofenceDetails> geofenceDetail = mDbManager.getGeofenceDetailsList(deviceNumber);
        LatLng latlngOld = new LatLng(geofenceDetail.get(geofenceDetail.size()-1).getLat(),geofenceDetail.get(geofenceDetail.size()-1).getLng());*/
        if (getArguments() != null) {
            lat = getArguments().getDouble(Constant.LATITUDE);
            lang = getArguments().getDouble(Constant.LONGNITUDE);
            int editGeofenceRadius = getArguments().getInt(Constant.GEOFENCE_RADIUS);
            if (!(editGeofenceRadius == 0)) {
                GEOFENCE_RADIUS_IN_METERS = editGeofenceRadius;
                geofenceDetail = mDbManager.getGeofenceDetailsList(deviceNumber);
                if (geofenceDetail != null && !geofenceDetail.isEmpty()) {
                    LatLng latlngOld = new LatLng(geofenceDetail.get(geofenceDetail.size() - 1).getLat(), geofenceDetail.get(geofenceDetail.size() - 1).getLng());
                    mDbManager.updateGeofenceDetailInGeofenceTable(geoFenceLatlng, GEOFENCE_RADIUS_IN_METERS, deviceNumber, latlngOld);
                }
            } else {
                GEOFENCE_RADIUS_IN_METERS = 5000;
                geoFenceLatlng = new LatLng(lat, lang);
                mDbManager.insertGeofenceDetailInGeofenceTable(geoFenceLatlng, GEOFENCE_RADIUS_IN_METERS, deviceNumber);
            }
        }
        new Thread(new FetchLocation()).start();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        MapsInitializer.initialize(getContext());
        mGeofencingClient = LocationServices.getGeofencingClient(getActivity());
        geofenceHelper = new GeofenceHelper(getActivity());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_MAPS);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.locationOnMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
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
        if (mapDataList != null && !mapDataList.isEmpty()) {
            trackeeLatlng = new LatLng(mapDataList.get(0).getLatitude(), mapDataList.get(0).getLongitude());
            addMarker(trackeeLatlng);
        }

        //GeofenceDetails geofenceDetails = mDbManager.getGeofenceDetails(deviceNumber);
        geofenceDetail = mDbManager.getGeofenceDetailsList(deviceNumber);
        if (geofenceDetail != null && !geofenceDetail.isEmpty()) {
            for (GeofenceDetails details : geofenceDetail) {
                geoFenceLatlng = new LatLng(details.getLat(), details.getLng());
                mapSettings(details.getRadius());
            }

        } else {
            showCustomAlertWithText(Constant.CREATE_GEOFENCE_ALERT);
            geoFenceLatlng = new LatLng(trackeeLatlng.latitude, trackeeLatlng.longitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(geoFenceLatlng, 12));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            addCircle(geoFenceLatlng, GEOFENCE_RADIUS_IN_METERS);
            addGeofence(geoFenceLatlng, GEOFENCE_RADIUS_IN_METERS);
            String address = Util.getAddressFromLocation(geoFenceLatlng.latitude, geoFenceLatlng.longitude, getActivity());
            float distance = distance((float) geoFenceLatlng.latitude, (float) geoFenceLatlng.longitude, (float) trackeeLatlng.latitude, (float) trackeeLatlng.longitude);
            int radiusDifference = (int) distance;
            trackGeofenceTransition(radiusDifference, address);
            //mapSettings();

        }
        /*mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(geoFenceLatlng, 12));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        addCircle(geoFenceLatlng, GEOFENCE_RADIUS_IN_METERS);
        addGeofence(geoFenceLatlng, GEOFENCE_RADIUS_IN_METERS);
        String address = Util.getAddressFromLocation(geoFenceLatlng.latitude,geoFenceLatlng.longitude,getActivity());
        float distance = distance((float) geoFenceLatlng.latitude, (float) geoFenceLatlng.longitude, (float) trackeeLatlng.latitude, (float) trackeeLatlng.longitude);
        int radiusDifference = (int) distance;
        trackGeofenceTransition(radiusDifference,address);*/


        //mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addCircle(LatLng latLng, int geofenceRadiusInMeters) {
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
    public void onMapClick(LatLng latLng) {
//        addMarker(trackeeLatlng);
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

                        mDbManager.insertGeofenceDetailInGeofenceTable(geoFenceLatlng, GEOFENCE_RADIUS_IN_METERS, deviceNumber);

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
        addMarker(trackeeLatlng);
        addCircle(latLng, GEOFENCE_RADIUS_IN_METERS);
        addGeofence(latLng, GEOFENCE_RADIUS_IN_METERS);
        float distance = distance((float) latLng.latitude, (float) latLng.longitude, (float) trackeeLatlng.latitude, (float) trackeeLatlng.longitude);
        int radiusDifference = (int) distance;
        trackGeofenceTransition(radiusDifference);
    }*/

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.secondaryuser));
        mMap.addMarker(markerOptions);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = null; //1 num of possible location returned
                try {
                    addresses = geocoder.getFromLocation(trackeeLatlng.latitude, trackeeLatlng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                //create your custom title
                String title = address + "-" + city + "-" + state;
                marker.setTitle(title);
                marker.showInfoWindow();

                return true;
            }
        });
    }

    /**
     * Fetch trackee location after every 1 min
     */
    public class FetchLocation implements Runnable {
        public void run() {
            while (true) {
                Log.d("Geofence", "call method in background");
                try {
                    makeApicallForTrackeeLocation();
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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

    public void makeApicallForTrackeeLocation() {
        SearchEventData searchEventData = new SearchEventData();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.LOCATION);
        mList.add(Constant.SOS);
        searchEventData.setTypes(mList);
        GroupRequestHandler.getInstance(getContext()).handleRequest(new SearchEventRequest(new SearchEventRequestSuccessListener(), new SearchEventRequestErrorListener(), searchEventData, mDbManager.getAdminLoginDetail().getUserId(), groupId, Constant.GET_LOCATION_URL));
    }

    /**
     * Search Event Request API call Success Listener
     */
    private class SearchEventRequestSuccessListener implements Response.Listener {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onResponse(Object response) {
            SearchEventResponse searchEventResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchEventResponse.class);
            List<SearchEventResponse.Data> mList = searchEventResponse.getData();
            if (!mList.isEmpty()) {
                List<GroupMemberDataList> grpMembersOfParticularGroupId = mDbManager.getAllGroupMemberDataBasedOnGroupId(groupId);
                for (SearchEventResponse.Data data : mList) {
                    for (GroupMemberDataList grpMembers : grpMembersOfParticularGroupId) {
                        if (grpMembers.getDeviceId() != null && grpMembers.getDeviceId().equalsIgnoreCase(data.getDevice())
                                && !grpMembers.getNumber().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getPhoneNumber())
                                && grpMembers.getUserId().equalsIgnoreCase(data.getUserId())) {
                            onMapReady(mMap);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
                            alertHistoryData = new AlertHistoryData();
                            String alertAddress = Util.getAddressFromLocation(data.getLocation().getLat(), data.getLocation().getLng(), getActivity());
                            alertHistoryData.setAddress(alertAddress);
                            alertHistoryData.setConsentId(grpMembers.getConsentId());
                            alertHistoryData.setDate(dateFormat.format(new Date()));
                            alertHistoryData.setName(grpMembers.getName());
                            alertHistoryData.setNumber(grpMembers.getNumber());
                            consentId = grpMembers.getConsentId();
                            trackeeLatlng = new LatLng(data.getLocation().getLat(), data.getLocation().getLng());
                            geofenceDetail = mDbManager.getGeofenceDetailsList(deviceNumber);
                            if (geofenceDetail != null && !geofenceDetail.isEmpty()) {
                                for (GeofenceDetails details : geofenceDetail) {
                                    geoFenceLatlng = new LatLng(details.getLat(), details.getLng());
                                    float distanceBetweenRadius = distance((float) geoFenceLatlng.latitude, (float) geoFenceLatlng.longitude, (float) trackeeLatlng.latitude, (float) trackeeLatlng.longitude);
                                    String address = Util.getAddressFromLocation(geoFenceLatlng.latitude, geoFenceLatlng.longitude, getActivity());
                                    trackGeofenceTransition((int) distanceBetweenRadius, address);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Search Event Request API Call Error listener
     */
    private class SearchEventRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            // Todo
        }
    }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void trackGeofenceTransition(int distance, String address) {
        NotificationHelper notificationHelper = new NotificationHelper(getActivity());
        if (distance < GEOFENCE_RADIUS_IN_METERS && GeofenceSettingsAcivity.geoFenceEntryNotificationFlag) {
            geoFenceEntryExit = true;
            if (alertHistoryData != null) {
                alertHistoryData.setState(Constant.ENTRY);
                mDbManager.insertIntoAlertHistoryTable(alertHistoryData);
            }
            notificationHelper.sendHighPriorityNotification(Constant.GEOFENCE_ENTRY_TITLE, memberName + Constant.GEOFENCE_ENTRY_MESSAGE + address + Constant.GEOFENCE_LIMIT, NotificationsAlertsActivity.class);
        } else if (distance > GEOFENCE_RADIUS_IN_METERS && geoFenceEntryExit && GeofenceSettingsAcivity.geoFenceExitNotificationFlag) {
            geoFenceEntryExit = false;
            if (alertHistoryData != null) {
                alertHistoryData.setState(Constant.EXIT);
                mDbManager.insertIntoAlertHistoryTable(alertHistoryData);
            }
            notificationHelper.sendHighPriorityNotification(Constant.GEOFENCE_EXIT_TITLE, memberName + Constant.GEOFENCE_EXIT_MESSAGE, NotificationsAlertsActivity.class);
        }
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage) {
        CustomAlertActivity alertActivity = new CustomAlertActivity(getContext());
        alertActivity.show();
        alertActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        alertActivity.alertWithOkButton(alertMessage);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void mapSettings(int radius) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(geoFenceLatlng, 15));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        addCircle(geoFenceLatlng, radius);
        addGeofence(geoFenceLatlng, radius);
        String address = Util.getAddressFromLocation(geoFenceLatlng.latitude, geoFenceLatlng.longitude, getActivity());
        float distance = distance((float) geoFenceLatlng.latitude, (float) geoFenceLatlng.longitude, (float) trackeeLatlng.latitude, (float) trackeeLatlng.longitude);
        int radiusDifference = (int) distance;
        trackGeofenceTransition(radiusDifference, address);
    }
}