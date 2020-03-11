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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.jio.devicetracker.R;
import com.jio.devicetracker.jiotoken.JioUtils;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.List;

public class RegistrationActivity extends Activity implements View.OnClickListener {

    private Button mRegistration;
    private EditText mJionmber;
    private EditText mName;
    private int permissionRequestCode = 100;
    private List<SubscriptionInfo> subscriptionInfos;
    public static boolean isFMSFlow = false;
    public static String phoneNumber = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.REGISTRATION_TITLE);

        mJionmber = findViewById(R.id.jioNumber);
        mName = findViewById(R.id.name);
        mRegistration = findViewById(R.id.registration);
        mRegistration.setOnClickListener(this);
        mJionmber.setOnClickListener(this);
        mJionmber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRegistration.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                mRegistration.setTextColor(Color.WHITE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String number = mJionmber.getText().toString();
                if (number.isEmpty()) {
                    mRegistration.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector, null));
                    mRegistration.setTextColor(Color.WHITE);
                } else {
                    mJionmber.setError(null);
                }
            }
        });

        requestPermission();
    }

    // Request for SMS and Phone Permissions
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.READ_PHONE_STATE}, permissionRequestCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (permissionRequestCode == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
            checkJioSIMSlot1();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.registration:
                validateNumber();
                break;
            default:
                break;

        }
    }

    private void validateNumber() {
        if (mName.getText().toString().equals("")) {
            mName.setError(Constant.NAME_VALIDATION);
        } else if (mJionmber.getText().toString().equals("")) {
            mJionmber.setError(Constant.PHONE_VALIDATION);
        } else {
            getssoToken();
        }
    }

    private void getssoToken() {
        boolean isAvailable = Util.isMobileNetworkAvailable(this);
        if (isAvailable) {
            checkJiooperator();

        } else {
            Util.alertDilogBox(Constant.MOBILE_NETWORKCHECK, Constant.ALERT_TITLE, this);
        }
    }

    /**
     * Checks the Jio SIM in slot 1 when user enters the mobile number.
     */
    private void checkJiooperator() {
        phoneNumber = mJionmber.getText().toString();
        String carierName = subscriptionInfos.get(0).getCarrierName().toString();
        String number = subscriptionInfos.get(0).getNumber();

        if (number != null
                && (number.equals(phoneNumber) || number.equals("91" + phoneNumber))) {
            if (carierName.contains(Constant.JIO)) {
                JioUtils.getSSOIdmaToken(this);
                gotoDashBoardActivity();
            } else {
                Util.alertDilogBox(Constant.NUMBER_VALIDATION, Constant.ALERT_TITLE, this);
            }
        } else if (subscriptionInfos.size() == 2 && subscriptionInfos.get(1).getNumber() != null) {
            if (subscriptionInfos.get(1).getNumber().equals("91" + phoneNumber) || subscriptionInfos.get(1).getNumber().equals(phoneNumber)) {
                Util.alertDilogBox(Constant.NUMBER_VALIDATION, Constant.ALERT_TITLE, this);
            } else {
                Util.alertDilogBox(Constant.DEVICE_JIONUMBER, Constant.ALERT_TITLE, this);
            }
        } else {
            Util.alertDilogBox(Constant.DEVICE_JIONUMBER, Constant.ALERT_TITLE, this);
        }

    }

    private void gotoDashBoardActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    /**
     * Checks the Jio SIM in slot 1 automatically
     */
    public void checkJioSIMSlot1() {
        if (subscriptionInfos != null) {
            String carrierNameSlot1 = subscriptionInfos.get(0).getCarrierName().toString();
            if (!carrierNameSlot1.contains(Constant.JIO)) {
                Util.alertDilogBox(Constant.SIM_VALIDATION, Constant.ALERT_TITLE, this);
            } else {
                mJionmber.setText(subscriptionInfos.get(0).getNumber().toString());
            }
        }
    }

}