package com.jio.devicetracker.view.geofence;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Marker;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GeofenceDetails;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GeofenceMapViewFragment extends Fragment implements OnMapReadyCallback {

    public static GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_MAPS = 101;
    public Toolbar toolbar;
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private static StringBuilder strAddress = null;
    private static Context context = null;
    private List<MapData> mapDataList;
    private String groupStatus;
    private boolean deviceLocation;
    private boolean peopleLocation;
    private String deviceNumber;
    private DBManager mDbManager;
    List<GeofenceDetails> geofenceDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        createNotificationChannel();
        context = getContext();
        strAddress = new StringBuilder();
        deviceLocation = getActivity().getIntent().getBooleanExtra(Constant.DEVICE_LOCATION, false);
        peopleLocation = getActivity().getIntent().getBooleanExtra(Constant.PEOPLE_LOCATION, false);
        mapDataList = getActivity().getIntent().getParcelableArrayListExtra(Constant.MAP_DATA);
        groupStatus = getActivity().getIntent().getStringExtra(Constant.GROUP_STATUS);
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();


        if (geofenceDetail != null && !geofenceDetail.isEmpty()) {
            for(GeofenceDetails details : geofenceDetail) {
                LatLng latlng = new LatLng(details.getLat(),details.getLng());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                addCircle(latlng,details.getRadius());
            }
        }
    }


        /**
         * Returns real address based on Lat and Long(Geo Coding)
         *
         * @param latitude
         * @param longitude
         * @return
         */
        private String getAddressFromLocation ( double latitude, double longitude){
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


       /* *//**
         * Called when you click on Markers, Markers will display the full address
         *
         * @param marker
         *//*
        @Override
        public void onInfoWindowClick (Marker marker){
            Toast.makeText(getContext(), strAddress.toString(), Toast.LENGTH_SHORT).show();
        }*/

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
         *//*
        public void showMapOnTimeInterval () {
            if (mMap != null) {
                onMapReady(mMap);
            }
        }*/

        /**
         * Creates the notification channel
         */
        private void createNotificationChannel () {
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

