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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.RegisterData;
import com.jio.devicetracker.database.pojo.RegisterRequestData;
import com.jio.devicetracker.database.pojo.request.RegistrationTokenrequest;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

import java.util.List;

/**
 * Implementation of Registration screen for admin registration.
 */
public class RegistrationDetailActivity extends Activity implements View.OnClickListener {

    private EditText mName;
    private EditText mEmail;
    private EditText mPhone;
    private int permissionRequestCode = 100;
    private EditText mPass;
    private EditText mRepass;
    private Button mRegister;
    private DBManager mDbmanager;
    private List<SubscriptionInfo> subscriptionInfos;
    public static String phoneNumber = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration_detail);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.REGISTRATION_TITLE);
        mName = findViewById(R.id.memberName);
        mEmail = findViewById(R.id.email);
        mPhone = findViewById(R.id.deviceNumber);
        mPass = findViewById(R.id.password);
        mRepass = findViewById(R.id.repassword);
        mRegister = findViewById(R.id.register);
        mDbmanager = new DBManager(this);
        mRegister.setOnClickListener(this);

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mName.getText().toString().equals("")) {
                    mRegister.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                    mRegister.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailId = mName.getText().toString();
                if (emailId.isEmpty()) {
                    mRegister.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector, null));
                    mRegister.setTextColor(Color.WHITE);
                }
            }
        });

        requestPermission();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.register:
                validate();
                break;
            default:
                break;
        }
    }

    private void validate() {

        if (mName.getText().toString().length() == 0) {
            mName.setError(Constant.NAME_EMPTY);
            return;
        }
        if (mEmail.getText().toString().length() == 0) {
            mEmail.setError(Constant.EMAIL_EMPTY);
            return;
        }

        if (mPhone.getText().toString().length() == 0) {
            mPhone.setError(Constant.MOBILE_NUMBER_EMPTY);
            return;
        }
        if (mPass.getText().toString().length() == 0) {
            mPass.setError(Constant.PASSWORD_EMPTY);
            return;
        }
        if (mRepass.getText().toString().length() == 0 || !mRepass.getText().toString().equals(mPass.getText().toString())) {
            mRepass.setError(Constant.PASSWORD_NOT_MATCHED);
            return;
        }

        if (mPhone.getText().toString().length() < 10) {
            mPhone.setError(Constant.VALID_PHONE_NUMBER);
        }
        if (mEmail.getText().toString().length() != 0 && !Util.isValidEmailId(mEmail.getText().toString().trim())) {
            mEmail.setError(Constant.EMAIL_VALIDATION);
            return;
        }

        if (mPass.getText().toString().length() != 0 && !Util.isValidPassword(mPass.getText().toString())) {
            mPass.setError(Constant.PASSWORD_VALIDATION2);
            return;
        }
        boolean jioCheck = getssoToken();
        if (jioCheck) {
            getServicecall();
        }
    }

    private void getServicecall() {
        RegisterRequestData data = new RegisterRequestData();
        data.setEmail(mEmail.getText().toString().trim());
        data.setType(Constant.REGISTRATION);
        data.setPhone(mPhone.getText().toString().trim().substring(2));
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new RegistrationTokenrequest(new SuccessListener(), new RegistrationDetailActivity.ErrorListener(), data));
    }

    private class SuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            int length = mPhone.getText().toString().trim().length();
            String phoneNumber = mPhone.getText().toString().trim().substring(2, length);
            RegisterData data = new RegisterData();
            data.setName(mName.getText().toString().trim());
            data.setEmail(mEmail.getText().toString().trim());
            data.setPhoneNumber(phoneNumber);
            data.setPassword(mPass.getText().toString());
            mDbmanager.insertAdminData(data);
            goToBorqsOTPActivity();
        }
    }

    private void goToBorqsOTPActivity() {
        startActivity(new Intent(this, BorqsOTPActivity.class));
    }

    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {

            if (error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                Util.alertDilogBox(Constant.REGISTRAION_ALERT_409, Constant.ALERT_TITLE, RegistrationDetailActivity.this);
            } else {
                Util.alertDilogBox(Constant.REGISTRAION_FAILED, Constant.ALERT_TITLE, RegistrationDetailActivity.this);

            }
        }
    }

    private boolean getssoToken() {
        boolean isAvailable = Util.isMobileNetworkAvailable(this);
        if (isAvailable) {
            return checkJiooperator();
        } else {
            Util.alertDilogBox(Constant.MOBILE_NETWORKCHECK, Constant.ALERT_TITLE, this);
            return false;
        }
    }

    private boolean checkJiooperator() {
        phoneNumber = mPhone.getText().toString();
        String carierName = subscriptionInfos.get(0).getCarrierName().toString();
        String number = subscriptionInfos.get(0).getNumber();
        if (number != null && (number.equals(phoneNumber) || number.equals("91" + phoneNumber))) {
            if (!carierName.contains(Constant.JIO)) {
                Util.alertDilogBox(Constant.NUMBER_VALIDATION, Constant.ALERT_TITLE, this);
                return false;
            } else if (subscriptionInfos.size() == 2 && subscriptionInfos.get(1).getNumber() != null) {
                if (subscriptionInfos.get(1).getNumber().equals("91" + phoneNumber) || subscriptionInfos.get(1).getNumber().equals(phoneNumber)) {
                    Util.alertDilogBox(Constant.NUMBER_VALIDATION, Constant.ALERT_TITLE, this);
                    return false;
                } else {
                    Util.alertDilogBox(Constant.DEVICE_JIONUMBER, Constant.ALERT_TITLE, this);
                    return false;
                }
            } else {
                Util.alertDilogBox(Constant.DEVICE_JIONUMBER, Constant.ALERT_TITLE, this);
                return false;
            }
        }
        return false;
    }

        private void requestPermission () {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, permissionRequestCode);
            }
        }

        public void onRequestPermissionsResult ( int requestCode, String permissions[],
        int[] grantResults){
            if (requestCode == permissionRequestCode) {
                if (ActivityCompat.checkSelfPermission(this, READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
                checkJioSIMSlot1();
            }
        }

        public void checkJioSIMSlot1 () {
            if (subscriptionInfos != null) {
                String carrierNameSlot1 = subscriptionInfos.get(0).getCarrierName().toString();
                if (!carrierNameSlot1.contains("Jio")) {
                    Util.alertDilogBox(Constant.SIM_VALIDATION, Constant.ALERT_TITLE, this);
                } else {
                    mPhone.setText(subscriptionInfos.get(0).getNumber().toString());
                    mPhone.setEnabled(false);
                }
            }
        }

    }
