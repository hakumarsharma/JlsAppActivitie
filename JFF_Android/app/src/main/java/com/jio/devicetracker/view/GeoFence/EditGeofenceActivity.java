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

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditGeofenceActivity  extends Activity implements View.OnClickListener {

    SeekBar radiusSeekBar;
    String  meterOrKiloMeter;
    private Button metersRadioButton;
    private Button kiloMetersRadioButton;
    private EditText locationName;
    private TextView radiusText;
    String geofenceAddress;
    private LatLng latlang;
    private String deviceNumber;
    private String memberName;
    int progressChangedValue=0;
    int radiusValue = 0;
    private List<MapData> mapDataList;
    private static final String TAG = "EditGeofenceActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_geofence);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.GEOFENCE_EDIT);
        meterOrKiloMeter = " km";

        radiusText = findViewById(R.id.radiusText);
        radiusText.setTypeface(Util.mTypeface(this,5));
        locationName = findViewById(R.id.location_name);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        metersRadioButton = findViewById(R.id.metersButton);
        metersRadioButton.setOnClickListener(this);
        kiloMetersRadioButton = findViewById(R.id.kiloMetersButton);
        kiloMetersRadioButton.setOnClickListener(this);
        Button updateBtn = findViewById(R.id.updateGeofence);
        updateBtn.setOnClickListener(this);
        Intent intent = getIntent();
        mapDataList = intent.getParcelableArrayListExtra(Constant.MAP_DATA);
        deviceNumber = intent.getStringExtra(Constant.DEVICE_NUMBER);
        memberName = intent.getStringExtra(Constant.MEMBER_NAME);
        geofenceAddress = intent.getStringExtra(Constant.GEOFENCE_ADDRESS);
        if(geofenceAddress != null){
            locationName.setText(geofenceAddress);
        }

        radiusSeekBar=(SeekBar)findViewById(R.id.radiusSeekBar);
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                radiusText.setText(progressChangedValue + meterOrKiloMeter );
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.metersButton){
            meterOrKiloMeter = " m";
            metersRadioButton.setBackgroundResource(R.drawable.radiobutton_selected);
            kiloMetersRadioButton.setBackgroundResource(R.drawable.radiobutton_unselected);
            radiusSeekBar.setMax(100);
            radiusSeekBar.setProgress(50);
            radiusText.setText("50 m");
        }else if (v.getId() == R.id.kiloMetersButton){
            meterOrKiloMeter = " km";
            metersRadioButton.setBackgroundResource(R.drawable.radiobutton_unselected);
            kiloMetersRadioButton.setBackgroundResource(R.drawable.radiobutton_selected);
            radiusSeekBar.setMax(20);
            radiusSeekBar.setProgress(10);
            radiusText.setText("10 km");
        }else if (v.getId() == R.id.updateGeofence){
            if(locationName.getText().toString().isEmpty()){
                Toast.makeText(this,"Please enter the location name",Toast.LENGTH_SHORT).show();
                return;
            }
            latlang = getLocationFromAddress(locationName.getText().toString());
            Log.d(TAG,"Update function implementation");
            if(latlang == null){
                Toast.makeText(this,Constant.ADDRESS_MESSAGE,Toast.LENGTH_SHORT).show();
                return;
            }
            String radius = radiusText.getText().toString();
            if(radius.contains("km")){
                radiusValue = progressChangedValue * 1000;
            } else {
                radiusValue = progressChangedValue;
            }
            Intent intent = new Intent(this,GeofenceActivity.class);
            intent.putExtra("Radius",radiusValue);
            intent.putExtra(Constant.LATITUDE,latlang.latitude);
            intent.putExtra(Constant.DEVICE_NUMBER,deviceNumber);
            intent.putExtra(Constant.MEMBER_NAME,memberName);
            intent.putParcelableArrayListExtra(Constant.MAP_DATA, (ArrayList<? extends Parcelable>) mapDataList);
            intent.putExtra(Constant.LONGNITUDE,latlang.longitude);

            intent.putExtra("EditGeofence",true);
            startActivityForResult(intent,100);

        } else {
            finish();
        }

    }

    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 10);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            finish();
        }
    }
}
