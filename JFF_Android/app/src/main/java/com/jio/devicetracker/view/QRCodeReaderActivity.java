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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;
import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Class which is responsible for reading the QR code
 */
public class QRCodeReaderActivity extends Activity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private boolean isComingFromAddContact;
    private boolean isComingFromAddDevice;
    private boolean isComingFromGroupList;
    private String userId;
    private String groupId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(R.layout.activity_qrcode_reader);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.SCAN_QR_CODE_TITLE);
        Button closeBtn = findViewById(R.id.close);
        closeBtn.setVisibility(View.VISIBLE);
        RelativeLayout toolbarLayout = findViewById(R.id.toolbarlayout);
        toolbarLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.cardviewlayout_device_background_color));
        Intent intent = getIntent();
        isComingFromAddDevice = intent.getBooleanExtra(Constant.IS_COMING_FROM_ADD_DEVICE, false);
        isComingFromAddContact = intent.getBooleanExtra(Constant.IS_COMING_FROM_ADD_CONTACT, false);
        isComingFromGroupList = intent.getBooleanExtra(Constant.IS_COMING_FROM_GROUP_LIST, false);
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        userId = intent.getStringExtra(Constant.USER_ID);

        RelativeLayout scanView = findViewById(R.id.scanframe);
        mScannerView = new ZXingScannerView(this);
        //mScannerView.setBackground(getDrawable(R.drawable.ic_qrcodescan));
        scanView.addView(mScannerView);
    }

    /**
     * Call back method, which navigates us to the ContactDetailsActivity
     *
     * @param result
     */
    @Override
    public void handleResult(Result result) {
        Intent intent = new Intent(this, DeviceNameActivity.class);
        intent.putExtra("QRCodeValue", result.getText());
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }
}
