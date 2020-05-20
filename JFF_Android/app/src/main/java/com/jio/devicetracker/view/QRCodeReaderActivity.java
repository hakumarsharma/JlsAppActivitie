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

import androidx.annotation.Nullable;

import com.google.zxing.Result;
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
        setContentView(mScannerView);
        Intent intent = getIntent();
        isComingFromAddDevice = intent.getBooleanExtra(Constant.IS_COMING_FROM_ADD_DEVICE, false);
        isComingFromAddContact = intent.getBooleanExtra(Constant.IS_COMING_FROM_ADD_CONTACT, false);
        isComingFromGroupList = intent.getBooleanExtra(Constant.IS_COMING_FROM_GROUP_LIST, false);
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        userId = intent.getStringExtra(Constant.USER_ID);
    }

    /**
     * Call back method, which navigates us to the ContactDetailsActivity
     *
     * @param result
     */
    @Override
    public void handleResult(Result result) {
        Intent intent = new Intent(this, ContactDetailsActivity.class);
        intent.putExtra(Constant.IS_COMING_FROM_ADD_CONTACT, isComingFromAddContact);
        intent.putExtra(Constant.IS_COMING_FROM_ADD_DEVICE, isComingFromAddDevice);
        intent.putExtra(Constant.IS_COMING_FROM_GROUP_LIST, isComingFromGroupList);
        intent.putExtra(Constant.GROUP_ID, groupId);
        intent.putExtra(Constant.USER_ID, userId);
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
