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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;


public class AttachDeviceActivity extends Activity implements View.OnClickListener {
    private EditText deviceNumber;
    private TextView errorText;
    private View underLine;
    private EditText deviceImei;
    private TextView imeiErrorText;
    private View imeiUnderLine;
    private static String groupId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_device);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.ATTACH_DEVICE_TITLE);
        title.setTypeface(Util.mTypeface(this,5));
        errorText = findViewById(R.id.number_validation_text);
        deviceNumber = findViewById(R.id.device_edit_name);
        underLine = findViewById(R.id.number_edit_line);

        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);

        imeiErrorText = findViewById(R.id.imei_validation_text);
        deviceImei = findViewById(R.id.device_edit_imei);
        imeiUnderLine = findViewById(R.id.imei_edit_line);


        Intent intent = getIntent();
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        editTextCallBackMethods();

    }

    private void editTextCallBackMethods(){
        Button connectBtn = findViewById(R.id.connect_btn);
        connectBtn.setOnClickListener(this);

        TextView deviceTitle = findViewById(R.id.device_number);
        TextView deviceImeiTitle = findViewById(R.id.device_imei);


        deviceNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Todo
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Todo
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    deviceTitle.setVisibility(View.VISIBLE);
                    connectBtn.setBackground(getResources().getDrawable(R.drawable.button_frame_blue));
                } else {
                    deviceTitle.setVisibility(View.INVISIBLE);
                    errorText.setVisibility(View.INVISIBLE);
                    underLine.setBackgroundColor(getResources().getColor(R.color.timerColor));
                }
            }
        });

        deviceImei.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Todo
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Todo
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    deviceImeiTitle.setVisibility(View.VISIBLE);
                    connectBtn.setBackground(getResources().getDrawable(R.drawable.button_frame_blue));
                } else {
                    deviceImeiTitle.setVisibility(View.INVISIBLE);
                    imeiErrorText.setVisibility(View.INVISIBLE);
                    imeiUnderLine.setBackgroundColor(getResources().getColor(R.color.timerColor));
                }
            }
        });
    }
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.back){
            finish();
        }

        if (deviceNumber.getText().toString().isEmpty() && deviceImei.getText().toString().isEmpty()){
            Toast.makeText(AttachDeviceActivity.this, Constant.ENTER_PHONE_OR_IMEI, Toast.LENGTH_LONG).show();
            return;
        }

        if (!deviceNumber.getText().toString().isEmpty() && !Util.isValidMobileNumberForPet(deviceNumber.getText().toString())){
                errorText.setVisibility(View.VISIBLE);
                underLine.setBackgroundColor(getResources().getColor(R.color.errorColor));
                imeiErrorText.setVisibility(View.INVISIBLE);
                imeiUnderLine.setBackgroundColor(getResources().getColor(R.color.timerColor));
                return;
        }

        if(!deviceImei.getText().toString().isEmpty() && !Util.isValidIMEINumber(deviceImei.getText().toString())){
            errorText.setVisibility(View.INVISIBLE);
            underLine.setBackgroundColor(getResources().getColor(R.color.timerColor));
            imeiErrorText.setVisibility(View.VISIBLE);
            imeiUnderLine.setBackgroundColor(getResources().getColor(R.color.errorColor));
            return;
        }

        errorText.setVisibility(View.INVISIBLE);
        underLine.setBackgroundColor(getResources().getColor(R.color.timerColor));
        imeiErrorText.setVisibility(View.INVISIBLE);
        imeiUnderLine.setBackgroundColor(getResources().getColor(R.color.timerColor));

        Intent intent = new Intent(this,DeviceNameActivity.class);
        intent.putExtra(Constant.GROUP_ID, groupId);
        intent.putExtra(Constant.DEVICE_PHONE_NUMBER,deviceNumber.getText().toString());
        intent.putExtra(Constant.DEVICE_IMEI_NUMBER,deviceImei.getText().toString());
        startActivity(intent);
    }
}
