// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
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
import android.util.Log;
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
import com.jio.devicetracker.jiotoken.JioUtilToken;
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
        title.setText("Registration");
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
                    mRegister.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.selector,null));
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
                Log.d("TAG", "Some other button is clicked");
                break;
        }
    }

    private void validate() {

        if (mName.getText().toString().length() == 0) {
            mName.setError("Name cannot be left blank.");
            return;
        }
        if (mEmail.getText().toString().length() == 0) {
            mEmail.setError("Email cannot be left blank.");
            return;
        }

        if (mPhone.getText().toString().length() == 0) {
            mPhone.setError("Mobile number cannot be left blank.");
            return;
        }
        if (mPass.getText().toString().length() == 0) {
            mPass.setError("Password cannot be left blank.");
            return;
        }
        if (mRepass.getText().toString().length() == 0 || !mRepass.getText().toString().equals(mPass.getText().toString())) {
            mRepass.setError("Password did not match, please try again");
            return;
        }

        if (mPhone.getText().toString().length() < 10) {
            mPhone.setError("Enter the valid phone number");
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
        data.setType("registration");
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

            if (error.networkResponse.statusCode == 409) {
                Util.alertDilogBox("User is already registered", "Jio Alert", RegistrationDetailActivity.this);
            } else {
                Util.alertDilogBox("Register failed ,Please contact your admin", "Jio Alert", RegistrationDetailActivity.this);
            }
        }
    }

    private boolean getssoToken() {
        boolean isAvailable = Util.isMobileNetworkAvailable(RegistrationDetailActivity.this);
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
            if (carierName.contains("Jio")) {
                JioUtilToken.getSSOIdmaToken(RegistrationDetailActivity.this);
                return true;
            } else {
                Util.alertDilogBox(Constant.NUMBER_VALIDATION, Constant.ALERT_TITLE, this);
                return false;
            }
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

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
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

    public void checkJioSIMSlot1() {
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
