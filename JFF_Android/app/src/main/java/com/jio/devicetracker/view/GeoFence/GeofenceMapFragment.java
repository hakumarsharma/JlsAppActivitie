package com.jio.devicetracker.view.geofence;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.database.pojo.response.SearchEventResponse;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class GeofenceMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {

    public static GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_MAPS = 101;
    public LocationManager locationManager;
    public static String TAG = "GeofenceMapFragment";
    public Toolbar toolbar;
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private static StringBuilder strAddress = null;
    private static Context context = null;
    private List<MapData> mapDataList;
    private LatLng latLng;
    private GeofenceHelper geofenceHelper;
    private MarkerOptions markerOptions;
    private final static int GEOFENCE_RADIUS_IN_METERS = 200;
    double Longitude = 26.422;
    double Latitude = 82.084;
    private GeofencingClient mGeofencingClient;
    private String GEOFENCE_ID = "JioTrack1";
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1000;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        if(getArguments() != null) {
            double lat = getArguments().getDouble(Constant.LATITUDE);
            double lang = getArguments().getDouble(Constant.LONGNITUDE);
            if (lat != 0 && lang != 0) {
                latLng = new LatLng( lat
                        , lang);

            }
        }
        context = getContext();
        strAddress = new StringBuilder();
        mapDataList = getActivity().getIntent().getParcelableArrayListExtra(Constant.MAP_DATA);

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
        //if (mapDataList.isEmpty()) {
            markerOptions = new MarkerOptions();
            if(latLng == null){
                latLng = new LatLng(Latitude, Longitude);
            }
            markerOptions.position(latLng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.addMarker(markerOptions);
            addCircle(latLng,GEOFENCE_RADIUS_IN_METERS);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addCircle(LatLng latLng, int geofenceRadiusInMeters) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(geofenceRadiusInMeters);
        circleOptions.strokeColor(Color.argb(1, 28, 96, 171));
        circleOptions.fillColor(Color.argb(.5f, 28, 96, 171));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);

    }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapClick(LatLng latLng) {
        handleMapLongClick(latLng);
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
            return null;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }

    /**
     * Refresh the map on particular time interval
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showMapOnTimeInterval() {
        if (mMap != null) {
            onMapReady(mMap);
        }
    }

    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        mGeofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                    .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "OnFailure: Geofence not added..."+errorMessage);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapLongClick(LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            handleMapLongClick(latLng);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleMapLongClick(LatLng latLng) {
        mMap.clear();
        addMarker(latLng);
        addCircle(latLng, GEOFENCE_RADIUS_IN_METERS);
        addGeofence(latLng, GEOFENCE_RADIUS_IN_METERS);
    }
    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }

}