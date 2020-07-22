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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;
import com.google.zxing.common.StringUtils;
import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Class which is responsible for reading the QR code
 */
public class QRCodeReaderActivity extends Activity implements ZXingScannerView.ResultHandler,View.OnClickListener {

    private ZXingScannerView mScannerView;
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
        closeBtn.setOnClickListener(this);
        RelativeLayout toolbarLayout = findViewById(R.id.toolbarlayout);
        toolbarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.cardviewlayout_device_background_color));
        Intent intent = getIntent();
        groupId = intent.getStringExtra(Constant.GROUP_ID);

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

//        if (result.getText().contains("<IMEI>") || result.getText().contains("<EAN>")){
//            Pattern pattern = Pattern.compile("<IMEI>(.+?)</IMEI>", Pattern.DOTALL);
//            Matcher matcher = pattern.matcher(result.getText());
//            System.out.println(matcher.group(1));
//        }
        String[] resultArr = result.getText().split("\n");
        if (resultArr.length == 2){
            if (Util.isValidIMEINumber(resultArr[0]) && Util.isValidMobileNumberForPet(resultArr[1])){
                goToDeviceActivityScreen(resultArr[0],resultArr[1]);
            }else {
                Intent intent = new Intent(this, QRCodeRescanActivity.class);
                startActivity(intent);
            }
        }else if (resultArr.length == 1 && (Util.isValidIMEINumber(resultArr[0]) || Util.isValidMobileNumberForPet(resultArr[0]))){
                goToDeviceActivityScreen(resultArr[0],resultArr[0]);
        }else {
            Intent intent = new Intent(this, QRCodeRescanActivity.class);
            startActivity(intent);
        }

    }

    private void goToDeviceActivityScreen(String imeiNumber,String phoneNumber){
        Intent intent = new Intent(this, DeviceNameActivity.class);
        intent.putExtra(Constant.GROUP_ID, groupId);
        intent.putExtra(Constant.DEVICE_PHONE_NUMBER, phoneNumber);
        intent.putExtra(Constant.DEVICE_IMEI_NUMBER, imeiNumber);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close){
            finish();
        }
    }
}
