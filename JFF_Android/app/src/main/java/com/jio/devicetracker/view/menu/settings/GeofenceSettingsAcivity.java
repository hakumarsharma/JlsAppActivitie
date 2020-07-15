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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class GeofenceSettingsAcivity extends Activity implements View.OnClickListener {
    public static boolean geoFenceEntryNotificationFlag;
    public static boolean geoFenceExitNotificationFlag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_settings);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.GEOFENCE);
        Button backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.VISIBLE);

        initUI();
    }

    private void initUI() {
        TextView geofenceEntry = findViewById(R.id.geofence_entry_text);
        geofenceEntry.setTypeface(Util.mTypeface(this, 5));
        TextView geofenceEntryDesc = findViewById(R.id.geofence_entry_desc_text);
        geofenceEntryDesc.setTypeface(Util.mTypeface(this, 3));
        TextView geofenceExit = findViewById(R.id.geofence_exit_text);
        geofenceExit.setTypeface(Util.mTypeface(this, 5));
        TextView geofenceExitDesc = findViewById(R.id.geofence_exit_desc_text);
        geofenceExitDesc.setTypeface(Util.mTypeface(this, 3));

        Switch geoentry = findViewById(R.id.entrySwitch);
        Switch geoexit = findViewById(R.id.exitSwitch);
        geoentry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                geoFenceEntryNotificationFlag = isChecked;
                // do something, the isChecked will be
                // true if the switch is in the On position
            }
        });
        geoexit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                geoFenceExitNotificationFlag = isChecked;
                // do something, the isChecked will be
                // true if the switch is in the On position
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        }
    }
}
