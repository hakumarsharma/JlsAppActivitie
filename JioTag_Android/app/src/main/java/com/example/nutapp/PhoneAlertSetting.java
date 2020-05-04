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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PhoneAlertSetting extends AppCompatActivity {

    RecyclerView m_recyclerView;
    PhoneAdapter m_phoneAlertAdapter;
    List<SettingsDetails> m_bleDetails = new ArrayList<SettingsDetails>();
    public static String HEXADDRRESS;


    public String getHexAddress(){
        return HEXADDRRESS;
    }

    public void addPhoneAlertItems() {
        SettingsDetails mainAlert = new SettingsDetails(getResources().getString(R.string.phonealert_main_header),"");
        SettingsDetails duration = new SettingsDetails(getResources().getString(R.string.phonealert_header_duration), getResources().getString(R.string.phonealert_header_duration_details));
        SettingsDetails repeat = new SettingsDetails(getResources().getString(R.string.phonealert_header_repeat), getResources().getString(R.string.phonealert_header_repeat_details));
        m_bleDetails.add(mainAlert);
        m_bleDetails.add(duration);
        m_bleDetails.add(repeat);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("ONCREATE","PHONEALERT");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_alert_recycle);
        Intent receiveIntent = this.getIntent();
        HEXADDRRESS = receiveIntent.getStringExtra("HEXADDR");

        m_recyclerView = (RecyclerView) findViewById(R.id.phonealert_recycle);
        addPhoneAlertItems();
        m_phoneAlertAdapter = new PhoneAdapter(m_bleDetails, this, m_recyclerView);
        m_recyclerView.setHasFixedSize(true);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        m_recyclerView.setAdapter(m_phoneAlertAdapter);

        ImageButton attachBack = (ImageButton) findViewById(R.id.phonealert_back);
        attachBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        ImageButton deviceHome = (ImageButton) findViewById(R.id.phonealert_home);
        deviceHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(JioUtils.HOME_KEY, intent);
                finish();
            }
        });
    }
}
