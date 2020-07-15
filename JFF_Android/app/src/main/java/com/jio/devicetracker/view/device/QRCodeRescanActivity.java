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

package com.jio.devicetracker.view.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;

public class QRCodeRescanActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescan_qrcode);
        initUI();
    }

    private void initUI() {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.DEVICE_NOT_FOUND_QR);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        Button rescanBtn = findViewById(R.id.scan_btn);
        rescanBtn.setOnClickListener(this);
        Button addManually = findViewById(R.id.manual_add);
        addManually.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.scan_btn:
                gotoQRReaderActivity();
                break;
            case R.id.manual_add:
                gotoAttachDeviceActivity();
                break;
            default:
                // Todo
                break;

        }
    }

    private void gotoAttachDeviceActivity() {
        Intent intent = new Intent(this, AttachDeviceActivity.class);
        startActivity(intent);
    }

    private void gotoQRReaderActivity() {
        Intent intent = new Intent(this, QRCodeReaderActivity.class);
        startActivity(intent);
    }
}
