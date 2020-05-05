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

import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.jio.devicetracker.database.pojo.GenerateTokenData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.Userdata;
import com.jio.devicetracker.database.pojo.request.AddDeviceRequest;
import com.jio.devicetracker.database.pojo.request.GenerateTokenRequest;
import com.jio.devicetracker.database.pojo.request.GetGroupInfoPerUserRequest;
import com.jio.devicetracker.database.pojo.request.LoginDataRequest;
import com.jio.devicetracker.database.pojo.response.AddDeviceResponse;
import com.jio.devicetracker.database.pojo.response.GenerateTokenResponse;
import com.jio.devicetracker.database.pojo.response.GetGroupInfoPerUserResponse;
import com.jio.devicetracker.database.pojo.response.LogindetailResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.network.MQTTManager;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.network.SendSMSTask;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of Splash Screen.This class creates splash screen for JFF application
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, MessageListener {

    private EditText jioMobileNumberEditText = null;
    private EditText jioUserNameEditText = null;
    private static EditText loginOtpEditText = null;
    public static String phoneNumber = null;
    public static LogindetailResponse logindetailResponse = null;
    private static final int PERMIT_ALL = 1;
    private String mbNumber;
    private String number;
    public static String userName;
    private static DBManager mDbManager;
    private List<SubscriptionInfo> subscriptionInfos;
    public static boolean isReadPhoneStatePermissionGranted = false;
    public static boolean isAccessCoarsePermissionGranted = false;
    private Button loginButton;
    private Locale locale = Locale.ENGLISH;
    private String userId;
    public static String ugsToken;

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
        jioMobileNumberEditText.setEnabled(false);
        jioMobileNumberEditText.setOnClickListener(this);
        mDbManager = new DBManager(this);
        boolean termConditionsFlag = Util.getTermconditionFlag(this);
        loginOtpEditText = findViewById(R.id.loginOtp);
        TextView registerHereTextView = findViewById(R.id.registedHere);
        registerHereTextView.setOnClickListener(this);
        MessageListener messageListener = new LoginActivity();
        MessageReceiver.bindListener(messageListener);
        checkTermandCondition(termConditionsFlag);
        checkJioSIMSlot1();
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
                }
            }
        });
    }

   /* *//**
     * Sends OTP to the user mobile number
     *
     * @param randomNumberForOTP
     *//*
    protected void sendSMSMessage(int randomNumberForOTP) {
        String phoneNo = number;
        String message = Constant.OTP_MESSAGE + randomNumberForOTP;

        if (0 == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), Constant.OTP_SENT,
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    Constant.SMS_SEND_FAILED, Toast.LENGTH_LONG).show();
            return;
        }

    }
*/
    /**
     * Gets called when you click on login button
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login) {
            validateNumber();
        } else if (v.getId() == R.id.registedHere) {
            checkJioSIMSlot1GotoRegistrationActivity();
        }
    }

    /*
     * Validate mobile number
     */
    private void validateNumber() {
        if ("".equals(jioUserNameEditText.getText().toString())) {
            jioUserNameEditText.setError(Constant.NAME_EMPTY);
            return;
        }
        if ("".equals(loginOtpEditText.getText().toString())) {
            loginOtpEditText.setError(Constant.EMPTY_OTP);
            return;
        } else {
            getssoToken();
        }
    }

    /**
     * Check network is there in phone or not
     */
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
        phoneNumber = jioMobileNumberEditText.getText().toString();
        if (subscriptionInfos != null) {
            String carierName = subscriptionInfos.get(0).getCarrierName().toString();
            String number = subscriptionInfos.get(0).getNumber();
            if (number != null && (number.equals(phoneNumber) || number.equals("91" + phoneNumber))) {
                if (carierName.toLowerCase(locale).contains(Constant.JIO)) {
                    onLoginButtonClick();
                } else if (!carierName.contains(Constant.JIO)) {
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
    }

    /**
     * Called when new register textview is clicked, number should be Jio number
     */
    private void checkJioSIMSlot1GotoRegistrationActivity() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
        if (subscriptionInfos != null) {
            String carrierNameSlot1 = subscriptionInfos.get(0).getCarrierName().toString();
            if (!carrierNameSlot1.toLowerCase(locale).contains(Constant.JIO)) {
                Util.alertDilogBox(Constant.SIM_VALIDATION, Constant.ALERT_TITLE, this);
            } else if (carrierNameSlot1.toLowerCase(locale).contains(Constant.JIO)) {
                generateTokenGotoRegistrationActivity();
            }
        }
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
        if (subscriptionInfos != null) {
            String carrierNameSlot1 = subscriptionInfos.get(0).getCarrierName().toString();
            if (!carrierNameSlot1.toLowerCase(locale).contains(Constant.JIO)) {
                Util.alertDilogBox(Constant.SIM_VALIDATION, Constant.ALERT_TITLE, this);
            } else {
                jioMobileNumberEditText.setText(subscriptionInfos.get(0).getNumber());
            }
        }
    }

    /**
     * Used to generate Token
     */
    private void generateTokenGotoRegistrationActivity() {
        GenerateTokenData generateTokenData = new GenerateTokenData();
        generateTokenData.setType(Constant.REGISTRATION);
        if (number != null) {
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
                Intent intent = new Intent(LoginActivity.this, BorqsTokenActivity.class);
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
                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
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
        Userdata data = new Userdata();
//        data.setToken(loginOtpEditText.getText().toString());
        data.setPassword("Borqs@1234");
        data.setPhone(number.substring(2));
        data.setPhoneCountryCode(number.substring(0, 2));
        data.setType(Constant.SUPERVISOR);
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new LoginDataRequest(new SuccessListener(), new ErrorListener(), data));
    }

    /**
     * Login successful Listener
     */
    private class SuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            logindetailResponse = Util.getInstance().getPojoObject(String.valueOf(response), LogindetailResponse.class);
            userId = logindetailResponse.getUser().getId();
            ugsToken = logindetailResponse.getUgsToken();
            if (logindetailResponse.getUgsToken() != null) {
                new DBManager(LoginActivity.this).insertLoginData(logindetailResponse);
            }
            // Verify and assign API Call
            makeMQTTConnection();
            makeVerifyAndAssignAPICall();
            // Get All Group info per user API Call
            GroupRequestHandler.getInstance(LoginActivity.this).handleRequest(new GetGroupInfoPerUserRequest(new GetGroupInfoPerUserRequestSuccessListener(), new GetGroupInfoPerUserRequestErrorListener(), userId));
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
     * GetGroupInfoPerUserRequest Success listener
     */
    private class GetGroupInfoPerUserRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GetGroupInfoPerUserResponse getGroupInfoPerUserResponse = Util.getInstance().getPojoObject(String.valueOf(response), GetGroupInfoPerUserResponse.class);
            parseResponseStoreInDatabase(getGroupInfoPerUserResponse);
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
        }
    }

    /**
     * GetGroupInfoPerUserRequest Error listener
     */
    private class GetGroupInfoPerUserRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 409) {
                Util.alertDilogBox(Constant.GROUP_LIMITATION, Constant.ALERT_TITLE, LoginActivity.this);
            }
        }
    }

    /**
     * Parse the response and store in DB(Group Table and Member table)
     */
    public void parseResponseStoreInDatabase(GetGroupInfoPerUserResponse getGroupInfoPerUserResponse) {
        List<HomeActivityListData> groupList = new ArrayList<>();
        List<GroupMemberDataList> mGroupMemberDataLists = new ArrayList<>();
        for (GetGroupInfoPerUserResponse.Data data : getGroupInfoPerUserResponse.getData()) {
                HomeActivityListData homeActivityListData = new HomeActivityListData();
                homeActivityListData.setGroupName(data.getGroupName());
                homeActivityListData.setCreatedBy(data.getCreatedBy());
                homeActivityListData.setGroupId(data.getId());
                homeActivityListData.setStatus(data.getStatus());
                homeActivityListData.setUpdatedBy(data.getUpdatedBy());
                groupList.add(homeActivityListData);
        }
        for (GetGroupInfoPerUserResponse.Data data : getGroupInfoPerUserResponse.getData()) {
            if(! data.getStatus().equalsIgnoreCase(Constant.CLOSED)) {
                for (GetGroupInfoPerUserResponse.Consents mConsents : data.getConsents()) {
                    GroupMemberDataList groupMemberDataList = new GroupMemberDataList();
                    groupMemberDataList.setConsentId(mConsents.getConsentId());
                    groupMemberDataList.setNumber(mConsents.getPhone());
                    groupMemberDataList.setGroupAdmin(mConsents.isGroupAdmin());
                    groupMemberDataList.setGroupId(data.getId());
                    groupMemberDataList.setConsentStatus(mConsents.getStatus());
                    groupMemberDataList.setName(mConsents.getName());
                    mGroupMemberDataLists.add(groupMemberDataList);
                }
            }
        }
        mDbManager.insertAllDataIntoGroupTable(groupList);
        mDbManager.insertGroupMemberDataInListFormat(mGroupMemberDataLists);
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

    // Gets called when app receives message
    @Override
    public void messageReceived(String message, String phoneNum) {
        /*String[] splitmessage = message.split(":");
        if (message.contains(Constant.OTP_MESSAGE) && jioMobileOtp != null) {
            jioMobileOtp.setText(splitmessage[1]);
        }*/
    }

    /**
     * Creates the MQTT connection with MQTT server
     */
    private void makeMQTTConnection() {
        MQTTManager mqttManager = new MQTTManager();
        mqttManager.getMQTTClient(this);
        mqttManager.connetMQTT();
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
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    isAccessCoarsePermissionGranted = false;
                }
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    isReadPhoneStatePermissionGranted = true;
                }

                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    isAccessCoarsePermissionGranted = true;
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
        devices.setMac(phoneNumber.substring(2));
        devices.setPhone(phoneNumber.substring(2));
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
}