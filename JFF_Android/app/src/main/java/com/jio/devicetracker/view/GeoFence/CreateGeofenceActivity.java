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
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GeofenceDetails;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CreateGeofenceActivity extends Activity implements View.OnClickListener {
    SeekBar radiusSeekBar;
    private TextView radiusText;
    int progressChangedValue=0;
    String  meterOrKiloMeter;
    private Button metersRadioButton;
    private Button kiloMetersRadioButton;
    private EditText locationName;
    private DBManager mDbManager;
    private String deviceNumber;
    private boolean createGeofenceFlag;
    private List<MapData> mapDataList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_geofence);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.GEOFENCE_CREATE);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        Button createGeofence = findViewById(R.id.updateGeofence);
        createGeofence.setText("Create");
        meterOrKiloMeter = " km";
        createGeofence.setOnClickListener(this);
        radiusSeekBar= findViewById(R.id.radiusSeekBar);
        locationName = findViewById(R.id.location_name);
        mDbManager = new DBManager(this);
        radiusText = findViewById(R.id.radiusText);
        radiusText.setTypeface(Util.mTypeface(this,5));
        radiusText.setText("5");
        Intent intent = getIntent();
        deviceNumber = intent.getStringExtra(Constant.DEVICE_NUMBER);
        createGeofenceFlag = intent.getBooleanExtra(Constant.CREATE_GEOFENCE,false);
        mapDataList = intent.getParcelableArrayListExtra(Constant.MAP_DATA);
        metersRadioButton = findViewById(R.id.metersButton);
        metersRadioButton.setOnClickListener(this);
        kiloMetersRadioButton = findViewById(R.id.kiloMetersButton);
        kiloMetersRadioButton.setOnClickListener(this);
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                radiusText.setText(String.valueOf(progressChangedValue));
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.updateGeofence:
                List<GeofenceDetails> details = mDbManager.getGeofenceDetailsList(deviceNumber);
                if(details.size()==10){
                    Toast.makeText(this,"Geofence limit is exceeded",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(locationName.getText().toString().isEmpty()){
                    Toast.makeText(this,"Please enter the location name",Toast.LENGTH_SHORT).show();
                    return;
                }
                LatLng latlang = Util.getLocationFromAddress(locationName.getText().toString(),this);
                if(latlang == null){
                    Toast.makeText(this,Constant.ADDRESS_MESSAGE,Toast.LENGTH_SHORT).show();
                    return;
                }
                String radius = radiusText.getText().toString();
                if(radius.isEmpty()){
                    Toast.makeText(this,"Please select the radius",Toast.LENGTH_SHORT).show();
                    return;
                }
                int geofenceRadius = 0;
                if(Integer.valueOf(radius) > 20){
                    geofenceRadius = Integer.valueOf(radius);
                } else {
                    geofenceRadius = Integer.valueOf(radius) * 1000;
                }

                mDbManager.insertGeofenceDetailInGeofenceTable(latlang, geofenceRadius, deviceNumber);
                if(createGeofenceFlag){
                    gotoGeofenceMapandListActivity();
                } else {
                    gotoGeofenceActivity();
                }

                break;
            case R.id.kiloMetersButton:
                meterOrKiloMeter = " km";
                metersRadioButton.setBackgroundResource(R.drawable.radiobutton_unselected);
                kiloMetersRadioButton.setBackgroundResource(R.drawable.radiobutton_selected);
                radiusSeekBar.setMax(20);
                radiusSeekBar.setProgress(5);
                radiusText.setText("5");
                break;
            case R.id.metersButton:
                meterOrKiloMeter = " m";
                metersRadioButton.setBackgroundResource(R.drawable.radiobutton_selected);
                kiloMetersRadioButton.setBackgroundResource(R.drawable.radiobutton_unselected);
                radiusSeekBar.setMax(100);
                radiusSeekBar.setProgress(50);
                radiusText.setText("50");
                break;

            default:
                break;

        }
    }

    private void gotoGeofenceActivity() {
        Intent intent = new Intent(this,GeofenceActivity.class);
        intent.putExtra(Constant.DEVICE_NUMBER,deviceNumber);
        intent.putExtra(Constant.CREATE_GEOFENCE,true);
        intent.putExtra(Constant.MEMBER_ADDRESS,locationName.getText().toString());
        intent.putParcelableArrayListExtra(Constant.MAP_DATA, (ArrayList<? extends Parcelable>) mapDataList);
        startActivityForResult(intent,160);
    }

    private void gotoGeofenceMapandListActivity() {
        Intent intent = new Intent(this,GeoFenceMapAndListViewActivity.class);
        intent.putExtra(Constant.DEVICE_NUMBER,deviceNumber);
        startActivityForResult(intent,210);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==210 || requestCode ==160){
            finish();
        } else {
            finish();
        }
    }
}
