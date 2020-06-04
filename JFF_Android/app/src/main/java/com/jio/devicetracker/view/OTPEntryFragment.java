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
import com.jio.devicetracker.database.pojo.GenerateLoginTokenData;
import com.jio.devicetracker.database.pojo.request.GenerateLoginTokenRequest;
import com.jio.devicetracker.database.pojo.response.GenerateTokenResponse;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

/**
 * OTP Entry Fragment
 */
public class OTPEntryFragment extends Fragment implements View.OnClickListener, MessageListener {

    private TextView enterOTPTextView;
    private PinEntryEditText pinEntryEditText;
    private Button submitLogin;
    private String phoneNumber;
    private TextView timerTextView;

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
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.timerTextView) {
            generateLoginTokenAPICall();
        } else if (v.getId() == R.id.submitLogin) {
            // Todo
        }
    }

    /**
     * Will be called when OTP is received in phone
     *
     * @param message
     * @param phoneNum
     */
    @Override
    public void messageReceived(String message, String phoneNum) {
        if (message.contains(Constant.OTP_MESSAGE) && pinEntryEditText != null) {
            pinEntryEditText.setText(message.substring(message.indexOf("OTP") + 6, message.indexOf("to") - 1));
        }
    }

    /**
     * Starts the timer for 60 second
     *
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

}
