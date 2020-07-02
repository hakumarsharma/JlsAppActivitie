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
        title.setTypeface(Util.mTypeface(this,5));
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RelativeLayout geofence = findViewById(R.id.geofence_app_layout);
        RelativeLayout lowbattery = findViewById(R.id.low_battery_layout);
        RelativeLayout sos = findViewById(R.id.sos_layout);
        RelativeLayout pollingfrequency = findViewById(R.id.polling_frequency_layout);
        TextView geofenceTitle = findViewById(R.id.geofence);
        TextView lowBatteryTitle = findViewById(R.id.low_battery);
        TextView sosTitle = findViewById(R.id.sos);
        TextView pollingFrequencyTitle = findViewById(R.id.polling_frequency);
        geofenceTitle.setTypeface(Util.mTypeface(this,5));
        lowBatteryTitle.setTypeface(Util.mTypeface(this,5));
        sosTitle.setTypeface(Util.mTypeface(this,5));
        pollingFrequencyTitle.setTypeface(Util.mTypeface(this,5));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.geofence_app_layout :
                Intent intent = new Intent(getApplicationContext(), GeofenceSettingsAcivity.class);
                startActivity(intent);
                break;
            case R.id.low_battery_layout :
                Intent batteryIntent = new Intent(getApplicationContext(), LowBatteryActivity.class);
                startActivity(batteryIntent);
                break;
            case R.id.sos_layout :
                break;
            case R.id.polling_frequency_layout :
                break;
        }
    }
}
