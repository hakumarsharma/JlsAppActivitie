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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class JioAddFinder extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_jio_finder);
        TextView addjioAppHeaderText= findViewById(R.id.addjio_app_header_text);
        TextView addjiolink= findViewById(R.id.addjiolink);
        Button btnSearchDevices= findViewById(R.id.addjio_btn_search_devices);
        btnSearchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain=new Intent(getApplicationContext(),QRReaderInstruction.class);
                startActivity(startMain);
            }
        });
        btnSearchDevices.setTypeface(JioUtils.mTypeface(this,5));
        addjioAppHeaderText.setTypeface(JioUtils.mTypeface(this,5));
        addjiolink.setTypeface(JioUtils.mTypeface(this,5));

        addjiolink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent startMain=new Intent(getApplicationContext(),JioPermissions.class);
                startActivity(startMain);
            }
        });
    }
}

