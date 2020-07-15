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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;

public class PollingFrequencyActivity extends Activity implements AdapterView.OnItemSelectedListener,View.OnClickListener {
    private String TAG = "PollingFrequencyActivity";

    String[] pollingFreq = { "5  |  min", "10  |  min", "15  |  min", "20  |  min", "25  |  min", "30  |  min", "35  |  min", "40  |  min", "45  |  min","50  |  min"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_frequency);

        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.POLLING_FREQUENCY);
        Button backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.VISIBLE);

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.freq_spinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the frequency list
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.spinner_item,pollingFreq);
        aa.setDropDownViewResource(R.layout.spinner_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

    }


    @Override
    public void onClick(View v) {
        Log.d(TAG,"provide clicklistner functionality");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG,"provide onItemSelected functionality");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(TAG,"provide onNothingSelected functionality");
    }
}
