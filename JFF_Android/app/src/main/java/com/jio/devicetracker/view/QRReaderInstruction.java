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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;

public class QRReaderInstruction extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan_instruction);
        RelativeLayout toolbarLayout = findViewById(R.id.toolbarlayout);
        toolbarLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.cardviewlayout_device_background_color));
        Button scanBtn = findViewById(R.id.scan_btn);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.SCAN_QR_CODE_TITLE);
        //title.setTypeface(JioUtils.mTypeface(this, 5));
        TextView scanHelpText = findViewById(R.id.qrcode_scan_help_title);
        //scanHelpText.setTypeface(JioUtils.mTypeface(this,3));
        Button addManually = findViewById(R.id.manual_add);
        addManually.setOnClickListener(this);
        scanBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_btn:
                gotoQRReaderScreen();
                break;

            case R.id.manual_add:
               // gotoJioPermissionScreen();
                break;

            default:
                break;
        }
    }

    /*private void gotoJioPermissionScreen() {
        Intent startMain = new Intent(getApplicationContext(), JioPermissions.class);
        startActivity(startMain);
    }*/

    private void gotoQRReaderScreen() {

        Intent startMain = new Intent(getApplicationContext(), QRCodeReaderActivity.class);
        startActivity(startMain);
    }
}
