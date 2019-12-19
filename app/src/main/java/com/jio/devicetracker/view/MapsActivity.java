// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.jio.devicetracker.database.pojo.response.TrackerdeviceResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    public static GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_MAPS = 101;
    public LocationManager locationManager;
    public Marker mCurrLocationMarker;
    public Toolbar toolbar;
    private static StringBuilder strAddress = null;
    private static Map<Double, Double> latLongMap = null;
    public static  int refreshIntervalTime = 300;

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

        if(mMap != null) {
            mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this));
            mMap.setOnInfoWindowClickListener(MapsActivity.this);
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
        int id = item.getItemId();
        switch (id){
            case R.id.oneminute:
                Toast.makeText(getApplicationContext(),"Location will be updated after every 1 minute",Toast.LENGTH_LONG).show();
                refreshIntervalTime = 60;
                return true;
            case R.id.twominute:
                Toast.makeText(getApplicationContext(),"Location will be updated after every 2 minute",Toast.LENGTH_LONG).show();
                refreshIntervalTime = 120;
                return true;
            case R.id.fiveminute:
                Toast.makeText(getApplicationContext(),"Location will be updated after every 5 minute",Toast.LENGTH_LONG).show();
                refreshIntervalTime = 300;
                return true;
            case R.id.tenminute:
                Toast.makeText(getApplicationContext(),"Location will be updated after every 10 minute",Toast.LENGTH_LONG).show();
                refreshIntervalTime = 600;
                return true;
            case R.id.fifminute:
                Toast.makeText(getApplicationContext(),"Location will be updated after every 15 minute",Toast.LENGTH_LONG).show();
                refreshIntervalTime = 900;
                return true;
            default:
                refreshIntervalTime = 300;
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        latLongMap = DashboardActivity.latLngMap;
        if (latLongMap.size() > 0 && strAddress != null) {
            for (Map.Entry<Double, Double> entry : latLongMap.entrySet()) {
                LatLng latLng = new LatLng(entry.getKey(), entry.getValue());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Jio Marker");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                markerOptions.snippet(getAddressFromLocation(entry.getKey(), entry.getValue()));
                mCurrLocationMarker = mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            myContentsView = inflater.inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }

    public void fetchLocationDetail() {
        List<TrackerdeviceResponse.Data> data = DashboardActivity.data;
        List<String> phoneNumner = DBManager.phoneNumner;
        latLongMap = new HashMap<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                if (phoneNumner.contains(data.get(i).getmDevice().getPhoneNumber())) {
                    latLongMap.put(data.get(i).getLocation().getLat(), data.get(i).getLocation().getLng());
                }
            }
        }
        if (mMap != null) {
            onMapReady(mMap);
        }
    }
}