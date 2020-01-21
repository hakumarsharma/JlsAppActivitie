// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Implementation of MapScreen to show the tracee's live location in map .
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    public static GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_MAPS = 101;
    public LocationManager locationManager;
    public Toolbar toolbar;
    private static StringBuilder strAddress = null;
    public static int refreshIntervalTime = 300;
    private Context context = null;
    Map<String, Map<Double, Double>> namingMap = null;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        strAddress = new StringBuilder();
        TextView title = findViewById(R.id.toolbar_title);
        title.setText("      Location");
        context = MapsActivity.this;

        if (mMap != null) {
            mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this));
            // mMap.setOnInfoWindowClickListener(MapsActivity.this);
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        MapsInitializer.initialize(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_MAPS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_interval_configuration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DashboardActivity dashboardActivity = new DashboardActivity();
        int id = item.getItemId();
        switch (id) {
            case R.id.oneminute:
                Toast.makeText(getApplicationContext(), "Location will be updated after every 1 minute", Toast.LENGTH_LONG).show();
                refreshIntervalTime = 60;
                dashboardActivity.startTheScheduler();
                return true;
            case R.id.twominute:
                Toast.makeText(getApplicationContext(), "Location will be updated after every 2 minutes", Toast.LENGTH_LONG).show();
                refreshIntervalTime = 120;
                dashboardActivity.startTheScheduler();
                return true;
            case R.id.fiveminute:
                Toast.makeText(getApplicationContext(), "Location will be updated after every 5 minutes", Toast.LENGTH_LONG).show();
                refreshIntervalTime = 300;
                dashboardActivity.startTheScheduler();
                return true;
            case R.id.tenminute:
                Toast.makeText(getApplicationContext(), "Location will be updated after every 10 minutes", Toast.LENGTH_LONG).show();
                refreshIntervalTime = 600;
                dashboardActivity.startTheScheduler();
                return true;
            case R.id.fifminute:
                Toast.makeText(getApplicationContext(), "Location will be updated after every 15 minutes", Toast.LENGTH_LONG).show();
                refreshIntervalTime = 900;
                dashboardActivity.startTheScheduler();
                return true;
            default:
                refreshIntervalTime = 300;
                dashboardActivity.startTheScheduler();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        namingMap = DashboardActivity.namingMap;
        int i = 0;
        List<Float> mapColorList = createColoredMapList();
        if (!namingMap.isEmpty() && strAddress != null) {
            for (Map.Entry<String, Map<Double, Double>> entry : namingMap.entrySet()) {
                MarkerOptions markerOptions = new MarkerOptions();
                for (Map.Entry<Double, Double> mapEntry : entry.getValue().entrySet()) {
                    LatLng latLng = new LatLng(mapEntry.getKey(), mapEntry.getValue());
                    markerOptions.position(latLng);
                    markerOptions.title(entry.getKey());
                    Log.d(entry.getKey() + " -->", "Value --> " + mapEntry.getKey() + " " + mapEntry.getValue());
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(mapColorList.get(i)));
                    markerOptions.snippet(getAddressFromLocation(mapEntry.getKey(), mapEntry.getValue()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.addMarker(markerOptions);
                    if (context != null) {
                        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(context));
                    }
                }
                if(i < 10) {
                    i++;
                }else{
                    i = 0;
                }
            }
        }
    }

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
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
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


    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, strAddress.toString(), Toast.LENGTH_SHORT).show();
    }

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

    public void showMapOnTimeInterval() {
        if(context != null) {
            DBManager mDbManager = new DBManager(context);
            List<AddedDeviceData> consentData = mDbManager.getAlldata(DashboardActivity.adminEmail);
            for (int i = 0; i < consentData.size(); i++) {
                for (int j = 0; j < DashboardActivity.selectedData.size(); j++) {
                    if (consentData.get(i).getPhoneNumber().equals(DashboardActivity.selectedData.get(j).getPhone())){
                        if (consentData.get(i).getConsentStaus().trim().equalsIgnoreCase("Yes JioTracker")){
                            DashboardActivity.latLngMap = mDbManager.getLatLongForMap(DashboardActivity.selectedData, consentData.get(i).getPhoneNumber());
                            DashboardActivity.namingMap.put(DashboardActivity.selectedData.get(i).getName(), DashboardActivity.latLngMap);
                        } else {
                            Util.alertDilogBox(Constant.CONSENT_NOTAPPROVED + consentData.get(i).getPhoneNumber(), Constant.ALERT_TITLE, this);
                        }
                    }
                }
            }
            if (mMap != null) {
                onMapReady(mMap);
            }
        }
    }
}