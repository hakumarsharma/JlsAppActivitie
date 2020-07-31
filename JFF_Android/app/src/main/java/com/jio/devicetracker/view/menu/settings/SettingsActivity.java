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

package com.jio.devicetracker.view.menu.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.dashboard.DashboardMainActivity;

public class SettingsActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initUI();
    }

    private void initUI() {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.SETTINGS);
        title.setTypeface(Util.mTypeface(this, 5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        RelativeLayout geofence = findViewById(R.id.geofence_app_layout);
        geofence.setOnClickListener(this);
        RelativeLayout lowbattery = findViewById(R.id.low_battery_layout);
        lowbattery.setOnClickListener(this);
        RelativeLayout sos = findViewById(R.id.sos_layout);
        sos.setOnClickListener(this);
        RelativeLayout pollingfrequency = findViewById(R.id.polling_frequency_layout);
        pollingfrequency.setOnClickListener(this);
        TextView geofenceTitle = findViewById(R.id.geofence);
        TextView lowBatteryTitle = findViewById(R.id.low_battery);
        TextView sosTitle = findViewById(R.id.sos);
        TextView pollingFrequencyTitle = findViewById(R.id.polling_frequency);
        geofenceTitle.setTypeface(Util.mTypeface(this, 5));
        lowBatteryTitle.setTypeface(Util.mTypeface(this, 5));
        sosTitle.setTypeface(Util.mTypeface(this, 5));
        pollingFrequencyTitle.setTypeface(Util.mTypeface(this, 5));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.geofence_app_layout:
                Intent intent = new Intent(getApplicationContext(), GeofenceSettingsAcivity.class);
                startActivity(intent);
                break;
            case R.id.low_battery_layout:
                Intent batteryIntent = new Intent(getApplicationContext(), LowBatteryActivity.class);
                startActivity(batteryIntent);
                break;
            case R.id.sos_layout:
                Intent sos = new Intent(getApplicationContext(), SOSActivity.class);
                startActivity(sos);
                break;
            case R.id.polling_frequency_layout:
                Intent pollingFreq = new Intent(getApplicationContext(), PollingFrequencyActivity.class);
                startActivity(pollingFreq);
                break;
            case R.id.back:
                Intent dashboardIntent = new Intent(this, DashboardMainActivity.class);
                dashboardIntent.putExtra(Constant.START_DRAWER, Constant.YES);
                startActivity(dashboardIntent);
//                startActivity(new Intent(this, DashboardMainActivity.class));
                break;
            default:
                // Todo
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent dashboardIntent = new Intent(this, DashboardMainActivity.class);
        dashboardIntent.putExtra(Constant.START_DRAWER, Constant.YES);
        startActivity(dashboardIntent);
    }

}
