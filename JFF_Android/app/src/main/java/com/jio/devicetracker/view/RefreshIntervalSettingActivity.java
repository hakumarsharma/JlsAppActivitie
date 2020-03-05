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

package com.jio.devicetracker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;

public class RefreshIntervalSettingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String[] refreshInterval = {Constant.TEN_SECONDS, Constant.ONE_MINUTE, Constant.TEN_MINUTES, Constant.FIFTEEN_MINUTES, Constant.ONE_HOUR};
    public static int refreshIntervalTime = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_interval);
        Toolbar toolbar = findViewById(R.id.refreshIntervalToolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.REFRESH_INTERVAL_SETTING);
        setSupportActionBar(toolbar);
        Spinner spin = findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, refreshInterval);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(arrayAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        DashboardActivity dashboardActivity = new DashboardActivity();
        switch (refreshInterval[position]) {
            case Constant.ONE_HOUR:
                refreshIntervalTime = 3600;
                dashboardActivity.startTheScheduler();
                break;
            case Constant.ONE_MINUTE:
                refreshIntervalTime = 60;
                dashboardActivity.startTheScheduler();
                break;
            case Constant.TEN_MINUTES:
                refreshIntervalTime = 600;
                dashboardActivity.startTheScheduler();
                break;
            case Constant.FIFTEEN_MINUTES:
                refreshIntervalTime = 900;
                dashboardActivity.startTheScheduler();
                break;
            default:
                refreshIntervalTime = 10;
                dashboardActivity.startTheScheduler();
                break;
        }
        Toast.makeText(getApplicationContext(), Constant.MAP_UPDATION_MSG + refreshInterval[position] , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Auto-generated method stub
    }
}
