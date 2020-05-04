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

package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    Double m_latitude;
    Double m_longitude;
    String m_addr;
    double defVal=0.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d("MAPS","onCreate");
        Intent receiveIntent = this.getIntent();
        m_latitude =receiveIntent.getDoubleExtra("LATITUDE",defVal);
        m_longitude =receiveIntent.getDoubleExtra("LONGITUDE",defVal);
        m_addr=receiveIntent.getStringExtra("ADDRESS");
        Log.d("LATLANG",m_latitude+":"+m_longitude);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ImageButton mapsBack = (ImageButton) findViewById(R.id.maps_back);
        mapsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        ImageButton mapsHome = (ImageButton) findViewById(R.id.maps_home);
        mapsHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("com.nutapp.notifications_maps_home_key");
                setResult(JioUtils.HOME_KEY, intent);
                sendBroadcast(intent);
                finish();
            }
        });

    }


    public String getDateTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        String datetime = dateformat.format(c.getTime());
        System.out.println(datetime);
        return datetime;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MAPS","onMAPREADY");
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        //String currentDateandTime = sdf.format(new Date());
        String currentDateandTime=getDateTime();

        GoogleMap mMap=googleMap;
        mMap.clear();
        mMap.setMyLocationEnabled(true);
        LatLng post = new LatLng(m_latitude, m_longitude);
        Log.d("MAPS",m_addr);
        Marker samMarker=mMap.addMarker(new MarkerOptions().title(m_addr).position(post).snippet(currentDateandTime)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        samMarker.setInfoWindowAnchor (0.5f, 3f);
        samMarker.showInfoWindow();
        float zoom=15;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(post, zoom));
    }
}

