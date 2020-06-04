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

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddDeviceData;
import com.jio.devicetracker.database.pojo.GenerateLoginTokenData;
import com.jio.devicetracker.database.pojo.LoginUserdata;
import com.jio.devicetracker.database.pojo.request.AddDeviceRequest;
import com.jio.devicetracker.database.pojo.request.GenerateLoginTokenRequest;
import com.jio.devicetracker.database.pojo.request.LoginDataRequest;
import com.jio.devicetracker.database.pojo.response.AddDeviceResponse;
import com.jio.devicetracker.database.pojo.response.GenerateTokenResponse;
import com.jio.devicetracker.database.pojo.response.LogindetailResponse;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * OTP Entry Fragment
 */
public class OTPEntryFragment extends Fragment implements View.OnClickListener, MessageListener {

    private TextView enterOTPTextView;
    private static PinEntryEditText pinEntryEditText;
    private Button submitLogin;
    private String phoneNumber;
    private TextView timerTextView;
    private DBManager mDbManager;
    private String userId;
    private String ugsToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        phoneNumber = getArguments().getString(Constant.MOBILE_NUMBER);
        MessageListener messageListener = new OTPEntryFragment();
        MessageReceiver.bindListener(messageListener);
        View view = inflater.inflate(R.layout.fragment_otp_entry, container, false);
        setLayoutData(view);
        startTimer(60000, 1000);
        return view;
    }

    /**
     * Sets Layout Data
     * @param view
     */
    private void setLayoutData(View view) {
        TextView sentOTPTextView = view.findViewById(R.id.sentOTPTextView);
        sentOTPTextView.setTypeface(Util.mTypeface(getActivity(), 5));
        enterOTPTextView = view.findViewById(R.id.enterOTPTextView);
        enterOTPTextView.setTypeface(Util.mTypeface(getActivity(), 3));
        enterOTPTextView.setText(Constant.OTP_TEXTVIEW + phoneNumber);
        pinEntryEditText = view.findViewById(R.id.txt_pin_entry);
        pinEntryEditText.setTypeface(Util.mTypeface(getActivity(), 5));
        submitLogin = view.findViewById(R.id.submitLogin);
        submitLogin.setTypeface(Util.mTypeface(getActivity(), 5));
        submitLogin.setOnClickListener(this);
        timerTextView = view.findViewById(R.id.timerTextView);
        timerTextView.setTypeface(Util.mTypeface(getActivity(), 2));
        timerTextView.setOnClickListener(this);
        mDbManager = new DBManager(getActivity());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.timerTextView) {
            generateLoginTokenAPICall();
        } else if (v.getId() == R.id.submitLogin) {
            onLoginButtonClick();
        }
    }

    /**
     * Will be called when OTP is received in phone
     * @param message
     * @param phoneNum
     */
    @Override
    public void messageReceived(String message, String phoneNum) {
        if (message.contains(Constant.OTP_SMS) && pinEntryEditText != null) {
            pinEntryEditText.setText(message.substring(message.indexOf(":") + 2, message.indexOf(":") + 7));
        }
    }

    /**
     * Starts the timer for 60 second
     * @param finish
     * @param tick
     */
    public void startTimer(final long finish, long tick) {
        CountDownTimer t = new CountDownTimer(finish, tick) {
            public void onTick(long millisUntilFinished) {
                long remainedSecs = millisUntilFinished / 1000 % 60;
                if (remainedSecs % 60 < 10) {
                    timerTextView.setText(Constant.RESEND_OTP + "00" + ":" + "0" + remainedSecs);
                } else {
                    timerTextView.setText(Constant.RESEND_OTP + "00" + ":" + remainedSecs);
                }
                timerTextView.setTextColor(getResources().getColor(R.color.unselected_button_background));
                timerTextView.setEnabled(false);
            }

            public void onFinish() {
                timerTextView.setText(Constant.REQUEST_OTP);
                timerTextView.setEnabled(true);
                timerTextView.setTextColor(getResources().getColor(R.color.timerTextViewColor));
                cancel();
            }
        }.start();
    }

    /**
     * Make an api call to generate Login token
     */
    private void generateLoginTokenAPICall() {
        GenerateLoginTokenData generateLoginTokenData = new GenerateLoginTokenData();
        GenerateLoginTokenData.Role role = new GenerateLoginTokenData().new Role();
        role.setCode(Constant.SUPERVISOR);
        generateLoginTokenData.setPhone(phoneNumber);
        generateLoginTokenData.setRole(role);
        Util.getInstance().showProgressBarDialog(getActivity());
        RequestHandler.getInstance(getActivity()).handleRequest(new GenerateLoginTokenRequest(new GenerateLoginTokenSuccessListener(), new GenerateLoginTokenErrorListener(), generateLoginTokenData));
    }

    /**
     * Generate Login token API call success listener
     */
    private class GenerateLoginTokenSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GenerateTokenResponse generateLoginTokenResponse = Util.getInstance().getPojoObject(String.valueOf(response), GenerateTokenResponse.class);
            Util.progressDialog.dismiss();
            if (generateLoginTokenResponse.getCode() == 200) {
                Toast.makeText(getActivity(), Constant.GENERATE_TOKEN_SUCCESS, Toast.LENGTH_SHORT).show();
                startTimer(60000, 1000);
            }
        }
    }

    /**
     * Generate Login Token API call error listener
     */
    private class GenerateLoginTokenErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            if (error.networkResponse.statusCode == 403) {
                pinEntryEditText.setError(Constant.GENERATE_TOKEN_FAILURE);
            }
        }
    }

    /**
     * Called when you click on Login button
     */
    private void onLoginButtonClick() {
        Util.getInstance().showProgressBarDialog(getActivity());
        LoginUserdata data = new LoginUserdata();
        LoginUserdata.Role role = new LoginUserdata().new Role();
        role.setCode(Constant.SUPERVISOR);
        data.setToken(pinEntryEditText.getText().toString().trim());
        data.setPhone(phoneNumber);
        RequestHandler.getInstance(getActivity()).handleRequest(new LoginDataRequest(new SuccessListener(), new ErrorListener(), data));
    }

    /**
     * Login successful Listener
     */
    private class SuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            LogindetailResponse logindetailResponse = Util.getInstance().getPojoObject(String.valueOf(response), LogindetailResponse.class);
            userId = logindetailResponse.getData().getId();
            ugsToken = logindetailResponse.getData().getUgsToken();
            Util.getAdminDetail(getActivity());
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
                Util.getAdminDetail(getActivity());
                startActivity(new Intent(getActivity(), DashboardMainActivity.class));
            }
        }
    }

    /**
     * Login unsuccessful Listener
     */
    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            if (error.networkResponse.statusCode == Constant.INVALID_USER) {
                Util.alertDilogBox(Constant.LOGIN_VALIDATION, Constant.ALERT_TITLE, getActivity());
            } else if (error.networkResponse.statusCode == Constant.ACCOUNT_LOCK) {
                Util.alertDilogBox(Constant.EMAIL_LOCKED, Constant.ALERT_TITLE, getActivity());
            } else {
                Util.alertDilogBox(Constant.VALID_USER, Constant.ALERT_TITLE, getActivity());
            }
            Util.progressDialog.dismiss();
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
        devices.setMac(phoneNumber);
        devices.setPhone(phoneNumber);
        devices.setIdentifier("imei");
        devices.setName("ABC");
        devices.setType("watch");
        devices.setModel("watch");
        AddDeviceData.Devices.Metaprofile metaprofile = new AddDeviceData().new Devices().new Metaprofile();
        metaprofile.setFirst("ABC");
        metaprofile.setSecond(Constant.SUCCESS);
        devices.setMetaprofile(metaprofile);
        AddDeviceData.Flags flags = new AddDeviceData().new Flags();
        flags.setSkipAddDeviceToGroup(false);
        addDeviceData.setFlags(flags);
        mList.add(devices);
        addDeviceData.setDevices(mList);
        RequestHandler.getInstance(getActivity()).handleRequest(new AddDeviceRequest(new AddDeviceRequestSuccessListener(), new AddDeviceRequestErrorListener(), ugsToken, userId, addDeviceData));
    }

    /**
     * Verify & Assign API call success listener
     */
    private class AddDeviceRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            AddDeviceResponse addDeviceResponse = Util.getInstance().getPojoObject(String.valueOf(response), AddDeviceResponse.class);
            if (addDeviceResponse.getCode() == 200) {
                Toast.makeText(getActivity(), Constant.SUCCESSFULL_DEVICE_ADDITION, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Verify & Assign API call error listener
     */
    private class AddDeviceRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getActivity(), Constant.UNSUCCESSFULL_DEVICE_ADDITION, Toast.LENGTH_SHORT).show();
        }
    }

}
