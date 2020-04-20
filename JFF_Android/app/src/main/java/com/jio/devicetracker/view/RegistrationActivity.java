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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.RegisterRequestData;
import com.jio.devicetracker.database.pojo.request.RegistrationTokenrequest;
import com.jio.devicetracker.database.pojo.response.RegistrationResponse;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.List;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

/**
 * Implementation of Registration screen for admin registration.
 */
public class RegistrationActivity extends Activity implements View.OnClickListener {

    private EditText mName;
    private EditText mPhone;
    private int permissionRequestCode = 100;
    private EditText mPass;
    private EditText mRepass;
    private Button mRegister;
    private DBManager mDbmanager;
    private List<SubscriptionInfo> subscriptionInfos;
    private String phoneNumber;
    private String countryCode;
    private Intent intent;
    private String receivedToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initializeDataMember();
        changeButtonColorAfterTextEnter();
        requestPermission();
    }

    /**
     * Use to initialize datamember
     */
    private void initializeDataMember() {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.REGISTRATION_TITLE);
        mName = findViewById(R.id.memberName);
        mPhone = findViewById(R.id.deviceNumber);
        mPass = findViewById(R.id.password);
        mRepass = findViewById(R.id.repassword);
        mRegister = findViewById(R.id.register);
        mDbmanager = new DBManager(this);
        mRegister.setOnClickListener(this);
        intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        countryCode = intent.getStringExtra("countryCode");
        receivedToken = intent.getStringExtra("token");
        mPhone.setText(countryCode+phoneNumber);
    }

    /**
     * Change Button color after entring value in edittext
     */
    private void changeButtonColorAfterTextEnter() {
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
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.register) {
            validate();
        }
    }

    /**
     * Validation check for the fields
     */
    private void validate() {
        if (mName.getText().toString().length() == 0) {
            mName.setError(Constant.NAME_EMPTY);
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
            return;
        }

        if (mPass.getText().toString().length() != 0 && !Util.isValidPassword(mPass.getText().toString())) {
            mPass.setError(Constant.PASSWORD_VALIDATION2);
            return;
        }
        getServicecall();
    }

    /**
     * Register API call
     */
    private void getServicecall() {
        String name = mName.getText().toString();
        RegisterRequestData registerRequestData = new RegisterRequestData();
        RegisterRequestData.Token token = new RegisterRequestData().new Token();
        token.setValue(receivedToken);
        RegisterRequestData.MetaProfile metaProfile = new RegisterRequestData().new MetaProfile();
        metaProfile.setName(name);
        registerRequestData.setName(name);
        registerRequestData.setPassword(mPass.getText().toString());
        registerRequestData.setPhone(phoneNumber);
        registerRequestData.setPhoneCountryCode(countryCode);
        registerRequestData.setMetaprofile(metaProfile);
        registerRequestData.setToken(token);
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new RegistrationTokenrequest(new SuccessListener(), new ErrorListener(), registerRequestData));
    }

    /**
     * Register API call success listener
     */
    private class SuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            RegistrationResponse registrationResponse = Util.getInstance().getPojoObject(String.valueOf(response), RegistrationResponse.class);
            if(registrationResponse.getCode() == Constant.SUCCESS_CODE_200 && registrationResponse.getMessage().equalsIgnoreCase(Constant.REGISTARTION_SUCCESS_MESSAGE)) {
                Toast.makeText(RegistrationActivity.this, Constant.REGISTARTION_SUCCESS_MESSAGE, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        }
    }

    /**
     * Register API call error listener
     */
    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                Util.alertDilogBox(Constant.REGISTRAION_ALERT_409, Constant.ALERT_TITLE, RegistrationActivity.this);
            } else {
                Util.alertDilogBox(Constant.REGISTRAION_FAILED, Constant.ALERT_TITLE, RegistrationActivity.this);
            }
        }
    }

    /**
     * Request for permission
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, permissionRequestCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == permissionRequestCode) {
            if (ActivityCompat.checkSelfPermission(this, READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }
}
