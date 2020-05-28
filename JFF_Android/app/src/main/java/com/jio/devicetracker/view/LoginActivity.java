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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddDeviceData;
import com.jio.devicetracker.database.pojo.GenerateLoginTokenData;
import com.jio.devicetracker.database.pojo.GenerateTokenData;
import com.jio.devicetracker.database.pojo.LoginUserdata;
import com.jio.devicetracker.database.pojo.request.AddDeviceRequest;
import com.jio.devicetracker.database.pojo.request.GenerateLoginTokenRequest;
import com.jio.devicetracker.database.pojo.request.GenerateTokenRequest;
import com.jio.devicetracker.database.pojo.request.LoginDataRequest;
import com.jio.devicetracker.database.pojo.response.AddDeviceResponse;
import com.jio.devicetracker.database.pojo.response.GenerateTokenResponse;
import com.jio.devicetracker.database.pojo.response.LogindetailResponse;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.network.SendSMSTask;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Splash Screen.This class creates splash screen for JFF application
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, MessageListener {

    private static EditText jioMobileNumberEditText = null;
    private EditText jioUserNameEditText = null;
    private static EditText loginOtpEditText = null;
    public static String phoneNumber = null;
    private List<SubscriptionInfo> subscriptionInfos;
    public static LogindetailResponse logindetailResponse = null;
    private static final int PERMIT_ALL = 1;
    private String mbNumber;
    private String number;
    public static String userName;
    private static DBManager mDbManager;
    public static boolean isReadPhoneStatePermissionGranted = false;
    private Button loginButton;
    private String userId;
    public static String ugsToken;
    private TextView requestOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestPermission();
        initializeDataMember();
        fetchMobileNumber();
    }

    /**
     * Use to initialize all the class member variables
     */
    private void initializeDataMember() {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.LOGIN_TITLE);
        loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(this);
        jioUserNameEditText = findViewById(R.id.userName);
        jioMobileNumberEditText = findViewById(R.id.jioNumber);
        jioMobileNumberEditText.setOnClickListener(this);
        mDbManager = new DBManager(this);
        boolean termConditionsFlag = Util.getTermconditionFlag(this);
        loginOtpEditText = findViewById(R.id.loginOtp);
        TextView registerHereTextView = findViewById(R.id.registerHere);
        requestOTP = findViewById(R.id.requestOTP);
        requestOTP.setOnClickListener(this);
        registerHereTextView.setOnClickListener(this);
        MessageListener messageListener = new LoginActivity();
        MessageReceiver.bindListener(messageListener);
        checkTermandCondition(termConditionsFlag);
        checkJioSIMSlot1();
        Util.getAdminDetail(this);
    }

    /**
     * Change the button color, when user enters something
     */
    private void fetchMobileNumber() {
        jioMobileNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                loginButton.setTextColor(Color.WHITE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                number = jioMobileNumberEditText.getText().toString();
                if (number.isEmpty()) {
                    loginButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector, null));
                    loginButton.setTextColor(Color.WHITE);
                } else {
                    jioMobileNumberEditText.setError(null);
                    SpannableString content = new SpannableString(Constant.REQUEST_OTP);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    requestOTP.setText(content);
                }
            }
        });
    }

    /**
     * Gets called when you click on login button
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
            if (v.getId() == R.id.login) {
                if(validateNumber()) {
                    onLoginButtonClick();
                }
            } else if (v.getId() == R.id.registerHere) {
                if (!Util.isValidMobileNumber(jioMobileNumberEditText.getText().toString().substring(2))) {
                    jioMobileNumberEditText.setError(Constant.MOBILENUMBER_VALIDATION);
                    return;
                }
                generateTokenGotoRegistrationActivity();
            } else if (v.getId() == R.id.requestOTP) {
                if (!Util.isValidMobileNumber(jioMobileNumberEditText.getText().toString().substring(2))) {
                    jioMobileNumberEditText.setError(Constant.MOBILENUMBER_VALIDATION);
                    return;
                }
                generateLoginTokenAPICall();
            }
    }

    /*
     * Validate mobile number, user name and OTP
     */
    private boolean validateNumber() {
        if ("".equals(jioUserNameEditText.getText().toString())) {
            jioUserNameEditText.setError(Constant.NAME_EMPTY);
            return false;
        } else if ("".equals(loginOtpEditText.getText().toString())) {
            loginOtpEditText.setError(Constant.EMPTY_OTP);
            return false;
        } else if (!Util.isValidMobileNumber(jioMobileNumberEditText.getText().toString().substring(2))) {
            jioMobileNumberEditText.setError(Constant.MOBILENUMBER_VALIDATION);
            return false;
        }
        return true;
    }

    /**
     * Checks the Jio SIM in slot 1 automatically
     */
    public void checkJioSIMSlot1() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
        jioMobileNumberEditText.setText(subscriptionInfos.get(0).getNumber());
        number = jioMobileNumberEditText.getText().toString();
    }


    /**
     * Used to generate Token
     */
    private void generateTokenGotoRegistrationActivity() {
        if (number != null) {
            GenerateTokenData generateTokenData = new GenerateTokenData();
            generateTokenData.setType(Constant.REGISTRATION);
            generateTokenData.setPhoneCountryCode(number.substring(0, 2));
            generateTokenData.setPhone(number.substring(2));
            RequestHandler.getInstance(getApplicationContext()).handleRequest(new GenerateTokenRequest(new GenerateTokenSuccessListener(), new GenerateTokenErrorListener(), generateTokenData));
        }
    }

    /**
     * Generate Token Success Listener
     */
    private class GenerateTokenSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GenerateTokenResponse generateTokenResponse = Util.getInstance().getPojoObject(String.valueOf(response), GenerateTokenResponse.class);
            if (generateTokenResponse.getCode() == Constant.SUCCESS_CODE_200 && generateTokenResponse.getMessage().equalsIgnoreCase(Constant.GENERATE_TOKEN_SUCCESS)) {
                Toast.makeText(LoginActivity.this, Constant.GENERATE_TOKEN_SUCCESS, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                intent.putExtra("countryCode", number.substring(0, 2));
                intent.putExtra("phoneNumber", number.substring(2));
                startActivity(intent);
            }
        }
    }

    /**
     * Generate Token error Listener
     */
    private class GenerateTokenErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 409) {
                Util.alertDilogBox(Constant.REGISTRAION_ALERT_409, Constant.ALERT_TITLE, LoginActivity.this);
                jioMobileNumberEditText.setError(Constant.REGISTRAION_ALERT_409);
                return;
            }
        }
    }

    // Request for SMS and Phone Permissions
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_CONTACTS}, PERMIT_ALL);
        }
    }

    /**
     * Called when you click on Login button
     */
    private void onLoginButtonClick() {
        Util.getInstance().showProgressBarDialog(this);
        userName = jioUserNameEditText.getText().toString().trim();
        LoginUserdata data = new LoginUserdata();
        LoginUserdata.Role role = new LoginUserdata().new Role();
        role.setCode(Constant.SUPERVISOR);
        data.setToken(loginOtpEditText.getText().toString().trim());
        data.setPhone(number.substring(2));
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new LoginDataRequest(new SuccessListener(), new ErrorListener(), data));
    }

    /**
     * Login successful Listener
     */
    private class SuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            logindetailResponse = Util.getInstance().getPojoObject(String.valueOf(response), LogindetailResponse.class);
            userId = logindetailResponse.getData().getId();
            ugsToken = logindetailResponse.getData().getUgsToken();

            // Verify and assign API Call if number is not already added on server
            if(mDbManager.getAdminLoginDetail() != null && mDbManager.getAdminLoginDetail().getPhoneNumber() != null
                    && logindetailResponse.getData().getPhone().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getPhoneNumber())) {
                System.out.println("Already added device it is");
            } else {
                makeVerifyAndAssignAPICall();
            }

            if (logindetailResponse.getData().getUgsToken() != null) {
                mDbManager.deleteAllPreviousData();
                mDbManager.insertLoginData(logindetailResponse);
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            }
        }
    }

    /**
     * Login unsuccessful Listener
     */
    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == Constant.INVALID_USER) {
                Util.alertDilogBox(Constant.LOGIN_VALIDATION, Constant.ALERT_TITLE, LoginActivity.this);
            } else if (error.networkResponse.statusCode == Constant.ACCOUNT_LOCK) {
                Util.alertDilogBox(Constant.EMAIL_LOCKED, Constant.ALERT_TITLE, LoginActivity.this);
            } else {
                Util.alertDilogBox(Constant.VALID_USER, Constant.ALERT_TITLE, LoginActivity.this);
            }
            Util.progressDialog.dismiss();
        }
    }

    /**
     * Checks the DeepLinking URI which tracker receives from tracee
     */
    private void deepLinkingURiCheck() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null && data.toString().contains("home")) {
            String[] splitStr = data.toString().split("=");
            String[] splitNamenumber = splitStr[1].split("&");
            mbNumber = splitNamenumber[0];
            /*String name = splitNamenumber[1];
            String imei = splitNamenumber[2];*/
            showDialog(mbNumber);
        }
    }

    /**
     * Display Dialog when you click on Deep link URI
     */
    public void showDialog(String number) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.number_display_dialog);
        dialog.setTitle(Constant.TITLE);
        dialog.getWindow().setLayout(1000, 500);
        // set the custom dialog components - text, image and button
        final Button yes = dialog.findViewById(R.id.positive);
        Button no = dialog.findViewById(R.id.negative);
        yes.setOnClickListener(v -> {
            //serviceCallLogin();
            String phoneNumber = null;
            if (subscriptionInfos != null) {
                phoneNumber = subscriptionInfos.get(0).getNumber();
            }
            new SendSMSTask().execute(mbNumber, Constant.YESJFF_SMS + phoneNumber.trim().substring(2, phoneNumber.length()));
            mDbManager.updateConsentInDeviceBors(mbNumber, Constant.CONSENT_APPROVED_STATUS);
            dialog.dismiss();
        });

        no.setOnClickListener(v -> {
            String phoneNumber = null;
            if (subscriptionInfos != null) {
                phoneNumber = subscriptionInfos.get(0).getNumber();
            }
            new SendSMSTask().execute(number, Constant.NOJFF_SMS + phoneNumber.trim().substring(2, phoneNumber.length()));
            mDbManager.updateConsentInDeviceBors(mbNumber, Constant.REQUEST_CONSENT);
            dialog.dismiss();
        });
        dialog.show();
    }

    // Gets called when Login page receives message
    @Override
    public void messageReceived(String message, String phoneNum) {
        if (message.contains(Constant.OTP_MESSAGE) && loginOtpEditText != null) {
            loginOtpEditText.setText(message.substring(message.indexOf("OTP") + 6, message.indexOf("for") - 1));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Checks privacy policy flag to show privacy policy screen
     */
    private void checkTermandCondition(boolean termConditionsFlag) {
        if (!termConditionsFlag) {
            checkTermConditionStatus();
        } else {
            deepLinkingURiCheck();
        }
    }

    private void checkTermConditionStatus() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(Constant.ALERT_TITLE);
        adb.setMessage(Constant.TERM_AND_CONDITION_STATUS_MSG);
        adb.setPositiveButton("OK", (dialog, which) -> {
            goToSplashnActivity();
        });

        adb.show();
    }

    private void goToSplashnActivity() {
        Intent intent = new Intent(this, SplashScreenActivity.class);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (PERMIT_ALL == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
            checkJioSIMSlot1();
        }
        switch (requestCode) {
            case PERMIT_ALL: {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    isReadPhoneStatePermissionGranted = false;
                }
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    isReadPhoneStatePermissionGranted = true;
                }
                subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
                // If request is cancelled, the result arrays are empty.
                for (int grantResult : grantResults) {
                    if (grantResults.length > 0 && grantResult == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permission is granted");
                    }
                }
                break;
            }
            default:
                break;
        }
    }


    /**
     * Verify and assign API Call for the white-listing of device
     */
    private void makeVerifyAndAssignAPICall() {
        AddDeviceData addDeviceData = new AddDeviceData();
        List<AddDeviceData.Devices> mList = new ArrayList<>();
        AddDeviceData.Devices devices = new AddDeviceData().new Devices();
        devices.setAge("31");
        devices.setGender("Male");
        devices.setHeight("6");
        devices.setWeight("70");
        devices.setMac(number.substring(2));
        devices.setPhone(number.substring(2));
        devices.setIdentifier("imei");
        devices.setName(userName);
        devices.setType("watch");
        devices.setModel("watch");
        AddDeviceData.Devices.Metaprofile metaprofile = new AddDeviceData().new Devices().new Metaprofile();
        metaprofile.setFirst(userName);
        metaprofile.setSecond("success");
        devices.setMetaprofile(metaprofile);
        AddDeviceData.Flags flags = new AddDeviceData().new Flags();
        flags.setSkipAddDeviceToGroup(false);
        addDeviceData.setFlags(flags);
        mList.add(devices);
        addDeviceData.setDevices(mList);
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new AddDeviceRequest(new AddDeviceRequestSuccessListener(), new AddDeviceRequestErrorListener(), ugsToken, userId, addDeviceData));
    }

    /**
     * Verify & Assign API call success listener
     */
    private class AddDeviceRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            AddDeviceResponse addDeviceResponse = Util.getInstance().getPojoObject(String.valueOf(response), AddDeviceResponse.class);
            if (addDeviceResponse.getCode() == 200) {
                Toast.makeText(LoginActivity.this, Constant.SUCCESSFULL_DEVICE_ADDITION, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Verify & Assign API call error listener
     */
    private class AddDeviceRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
//            Toast.makeText(LoginActivity.this, Constant.UNSUCCESSFULL_DEVICE_ADDITION, Toast.LENGTH_SHORT).show();
        }
    }

    private void generateLoginTokenAPICall() {
        GenerateLoginTokenData generateLoginTokenData = new GenerateLoginTokenData();
        GenerateLoginTokenData.Role role = new GenerateLoginTokenData().new Role();
        role.setCode(Constant.SUPERVISOR);
        generateLoginTokenData.setPhone(number.substring(2));
        generateLoginTokenData.setRole(role);
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new GenerateLoginTokenRequest(new GenerateLoginTokenSuccessListener(), new GenerateLoginTokenErrorListener(), generateLoginTokenData));
    }

    /**
     * Generate Login token API call success listener
     */
    private class GenerateLoginTokenSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GenerateTokenResponse generateLoginTokenResponse = Util.getInstance().getPojoObject(String.valueOf(response), GenerateTokenResponse.class);
            if (generateLoginTokenResponse.getCode() == 200) {
                Toast.makeText(LoginActivity.this, Constant.GENERATE_TOKEN_SUCCESS, Toast.LENGTH_SHORT).show();
                SpannableString content = new SpannableString(Constant.RESEND_OTP);
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                requestOTP.setText(content);
            }
        }
    }

    /**
     * Generate Login Token API call error listener
     */
    private class GenerateLoginTokenErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 403) {
                jioMobileNumberEditText.setError(Constant.GENERATE_TOKEN_FAILURE);
            }
        }
    }
}