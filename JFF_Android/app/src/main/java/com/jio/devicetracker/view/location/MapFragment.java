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

package com.jio.devicetracker.view.location;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    public static GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_MAPS = 101;
    public LocationManager locationManager;
    public Toolbar toolbar;
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private static StringBuilder strAddress = null;
    private static Context context = null;
    private List<MapData> mapDataList;
    private String groupStatus;
    private boolean deviceLocation;
    private boolean peopleLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        createNotificationChannel();
        context = getContext();
        strAddress = new StringBuilder();
        deviceLocation = getActivity().getIntent().getBooleanExtra(Constant.DEVICE_LOCATION,false);
        peopleLocation = getActivity().getIntent().getBooleanExtra(Constant.PEOPLE_LOCATION,false);
        mapDataList = getActivity().getIntent().getParcelableArrayListExtra(Constant.MAP_DATA);
        groupStatus = getActivity().getIntent().getStringExtra(Constant.GROUP_STATUS);
        if (mMap != null) {
            mMap.setInfoWindowAdapter(new MapFragment.MyInfoWindowAdapter(getContext()));
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.locationOnMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        MapsInitializer.initialize(getContext());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_MAPS);
        }


        return view;
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage) {
        CustomAlertActivity alertActivity = new CustomAlertActivity(getContext());
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

    /**
     * Call back method to load the map on screen
     * Show the alerts if group is not active or it is completed
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        if (groupStatus.equalsIgnoreCase(Constant.SCHEDULED)) {
            showCustomAlertWithText(Constant.GROUP_NOT_ACTIVE);
        } else if (groupStatus.equalsIgnoreCase(Constant.COMPLETED)) {
            showCustomAlertWithText(Constant.SESSION_COMPLETED);
        } else if (mapDataList.isEmpty() && peopleLocation) {
            showCustomAlertWithText(Constant.FETCH_LOCATION_ERROR);
        } else if(mapDataList.isEmpty() && deviceLocation){
            showCustomAlertWithText(Constant.FETCH_DEVICE_LOCATION_ERROR);
        }
        if (mapDataList != null && !mapDataList.isEmpty() && strAddress != null && groupStatus.equalsIgnoreCase(Constant.ACTIVE)) {
            for (MapData mapData : mapDataList) {
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(mapData.getLatitude(), mapData.getLongitude());
                markerOptions.position(latLng);
                markerOptions.title(mapData.getName());
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.secondaryuser));
                markerOptions.snippet(getAddressFromLocation(mapData.getLatitude(), mapData.getLongitude()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.addMarker(markerOptions);
                if (context != null) {
                    mMap.setInfoWindowAdapter(new MapFragment.MyInfoWindowAdapter(context));
                }
            }
        }
    }

    /**
     * Adds 10 color in list, so that we could be able to display
     * different marker with different color
     *
     * @return List of available colors
     *//*
    private List<Float> createColoredMapList() {
        List<Float> mapColorList = new ArrayList<>();
        mapColorList.add(BitmapDescriptorFactory.HUE_BLUE);
        mapColorList.add(BitmapDescriptorFactory.HUE_GREEN);
        mapColorList.add(BitmapDescriptorFactory.HUE_ORANGE);
        mapColorList.add(BitmapDescriptorFactory.HUE_AZURE);
        mapColorList.add(BitmapDescriptorFactory.HUE_ROSE);
        mapColorList.add(BitmapDescriptorFactory.HUE_CYAN);
        mapColorList.add(BitmapDescriptorFactory.HUE_MAGENTA);
        mapColorList.add(BitmapDescriptorFactory.HUE_RED);
        mapColorList.add(BitmapDescriptorFactory.HUE_VIOLET);
        mapColorList.add(BitmapDescriptorFactory.HUE_YELLOW);
        return mapColorList;
    }*/

    /**
     * Returns real address based on Lat and Long(Geo Coding)
     *
     * @param latitude
     * @param longitude
     * @return
     */
    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address fetchedAddress = addresses.get(0);
                strAddress.setLength(0);
                strAddress.append(fetchedAddress.getAddressLine(0)).append(" ");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return strAddress.toString();
    }


    /**
     * Called when you click on Markers, Markers will display the full address
     *
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getContext(), strAddress.toString(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Class to show address when you click on marker
     */
    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View myContentsView;

        MyInfoWindowAdapter(Context context) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myContentsView = inflater.inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tvTitle = myContentsView.findViewById(R.id.title);
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = myContentsView.findViewById(R.id.snippet);
            tvSnippet.setText(marker.getSnippet());
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }

    /**
     * Refresh the map on particular time interval
     */
    public void showMapOnTimeInterval() {
        if (mMap != null) {
            onMapReady(mMap);
        }
    }

    /**
     * Creates the notification channel
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constant.NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
