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

package com.jio.devicetracker.view.signinsignup;

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
import com.google.android.gms.safetynet.SafetyNet;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.GenerateTokenData;
import com.jio.devicetracker.database.pojo.RegisterRequestData;
import com.jio.devicetracker.database.pojo.request.GenerateTokenRequest;
import com.jio.devicetracker.database.pojo.request.RegistrationTokenrequest;
import com.jio.devicetracker.database.pojo.response.GenerateTokenResponse;
import com.jio.devicetracker.database.pojo.response.RegistrationResponse;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;

import java.util.Objects;

/**
 * Signup OTP Fragment
 */
public class SignupOTPFragment extends Fragment implements View.OnClickListener, MessageListener {

    private String phoneNumber;
    private static PinEntryEditText signupTxtPinEntry;
    private TextView signupTimerTextView;
    private String name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        phoneNumber = getArguments().getString(Constant.MOBILE_NUMBER);
        name = getArguments().getString(Constant.NAME);
        View view = inflater.inflate(R.layout.fragment_signup_otp, container, false);
        setLayoutData(view);
        return view;
    }

    private void setLayoutData(View view) {
        TextView signupSentOTPTextView = view.findViewById(R.id.signupSentOTPTextView);
        signupSentOTPTextView.setTypeface(Util.mTypeface(getActivity(), 5));
        TextView signupEnterOTPTextView = view.findViewById(R.id.signupEnterOTPTextView);
        signupEnterOTPTextView.setTypeface(Util.mTypeface(getActivity(), 3));
        signupEnterOTPTextView.setText(Constant.OTP_TEXTVIEW + phoneNumber);
        signupTxtPinEntry = view.findViewById(R.id.signup_txt_pin_entry);
        signupTxtPinEntry.setTypeface(Util.mTypeface(getActivity(), 5));
        Button signupSubmitLogin = view.findViewById(R.id.signupSubmitLogin);
        signupSubmitLogin.setTypeface(Util.mTypeface(getActivity(), 5));
        signupSubmitLogin.setOnClickListener(this);
        signupTimerTextView = view.findViewById(R.id.signupTimerTextView);
        signupTimerTextView.setTypeface(Util.mTypeface(getActivity(), 2));
        signupTimerTextView.setOnClickListener(this);
        MessageListener messageListener = new SignupOTPFragment();
        MessageReceiver.bindListener(messageListener);
        startTimer(60000, 1000);
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage){
        CustomAlertActivity alertActivity = new CustomAlertActivity(getContext());
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

    @Override
    public void messageReceived(String message, String phoneNum) {
        if (message.contains(Constant.OTP_SMS) && signupTxtPinEntry != null) {
            signupTxtPinEntry.setText(message.substring(message.indexOf(":") + 2, message.indexOf(":") + 7));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signupSubmitLogin) {
            String token = signupTxtPinEntry.getText().toString().trim();
            if (!token.equalsIgnoreCase(Constant.EMPTY_STRING)) {
                makeSafetyNetCall(token);
            }
        } else if(v.getId() == R.id.signupTimerTextView) {
            String token = signupTxtPinEntry.getText().toString().trim();
            makeSafetyNetCall(token);
        }
    }

    private void makeSafetyNetCall(String token) {
        SafetyNet.getClient(Objects.requireNonNull(getActivity())).verifyWithRecaptcha(Constant.GOOGLE_RECAPCHA_KEY)
                .addOnSuccessListener(getActivity(), response -> {
                    if (token == null || token.isEmpty()){
                        if (!response.getTokenResult().isEmpty()) {
                            Util.getInstance().setExpiryTime();
                            Util.getInstance().updateGoogleToken(response.getTokenResult());
                            generateRegistrationTokenAPICall();
                        }
                    }else {
                        if (!response.getTokenResult().isEmpty()) {
                            Util.getInstance().setExpiryTime();
                            Util.getInstance().updateGoogleToken(response.getTokenResult());
                            makeRegisterAPICall(token);
                        }
                    }
                })
                .addOnFailureListener(getActivity(), e -> Toast.makeText(getActivity(), Constant.GOOGLE_RECAPTCHA_ERROR, Toast.LENGTH_SHORT).show());
    }

    /**
     * Used to generate registration Token
     */
    private void generateRegistrationTokenAPICall() {
        if (phoneNumber != null) {
            GenerateTokenData generateTokenData = new GenerateTokenData();
            generateTokenData.setType(Constant.REGISTRATION);
            generateTokenData.setPhoneCountryCode(Constant.COUNTRY_CODE);
            generateTokenData.setPhone(phoneNumber);
            RequestHandler.getInstance(getActivity()).handleRequest(new GenerateTokenRequest(new GenerateTokenSuccessListener(), new GenerateTokenErrorListener(), generateTokenData));
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
//                Toast.makeText(getActivity(), Constant.GENERATE_TOKEN_SUCCESS, Toast.LENGTH_SHORT).show();
                startTimer(60000, 1000);
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
                showCustomAlertWithText(Constant.REGISTRAION_ALERT_409);
                return;
            }
        }
    }

    /**
     * Register API call
     */
    private void makeRegisterAPICall(String otp) {
        RegisterRequestData registerRequestData = new RegisterRequestData();
        RegisterRequestData.Token token = new RegisterRequestData().new Token();
        token.setValue(otp);
        RegisterRequestData.MetaProfile metaProfile = new RegisterRequestData().new MetaProfile();
        metaProfile.setName(name);
        registerRequestData.setName(name);
        registerRequestData.setPhone(phoneNumber);
        registerRequestData.setPhoneCountryCode(Constant.COUNTRY_CODE);
        registerRequestData.setMetaprofile(metaProfile);
        registerRequestData.setToken(token);
        Util.getInstance().showProgressBarDialog(getActivity(), Constant.LOADING_DATA);
        RequestHandler.getInstance(getActivity()).handleRequest(new RegistrationTokenrequest(new SuccessListener(), new ErrorListener(), registerRequestData));
    }

    /**
     * Register API call success listener
     */
    private class SuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            RegistrationResponse registrationResponse = Util.getInstance().getPojoObject(String.valueOf(response), RegistrationResponse.class);
            Util.progressDialog.dismiss();
            if (registrationResponse.getCode() == Constant.SUCCESS_CODE_200) {
                Toast.makeText(getActivity(), Constant.REGISTARTION_SUCCESS_MESSAGE, Toast.LENGTH_SHORT).show();
                // Switch to the Login Fragment once registration is successfull
                startActivity(new Intent(getActivity(), SigninSignupActivity.class));
                /*LoginFragment loginFragment = new LoginFragment();
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                trans.replace(R.id.loginFrameLayout, loginFragment);
                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                trans.addToBackStack(null);
                trans.commit();*/
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
                showCustomAlertWithText(Constant.REGISTRAION_ALERT_409);
            } else {
                showCustomAlertWithText(Constant.REGISTRAION_FAILED);
            }
        }
    }

    /**
     * Starts the timer for 60 second
     * @param finish
     * @param tick
     */
    public void startTimer(final long finish, long tick) {
        new CountDownTimer(finish, tick) {
            public void onTick(long millisUntilFinished) {
                long remainedSecs = millisUntilFinished / 1000 % 60;
                if (remainedSecs % 60 < 10) {
                    signupTimerTextView.setText(Constant.RESEND_OTP + "00" + ":" + "0" + remainedSecs);
                } else {
                    signupTimerTextView.setText(Constant.RESEND_OTP + "00" + ":" + remainedSecs);
                }
                signupTimerTextView.setTextColor(getResources().getColor(R.color.unselected_button_background));
                signupTimerTextView.setEnabled(false);
            }

            public void onFinish() {
                signupTxtPinEntry.getText().clear();
                signupTimerTextView.setText(Constant.REQUEST_OTP);
                signupTimerTextView.setEnabled(true);
                signupTimerTextView.setTextColor(getResources().getColor(R.color.timerTextViewColor));
                cancel();
            }
        }.start();
    }

}
