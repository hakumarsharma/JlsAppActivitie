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

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.safetynet.SafetyNet;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.GenerateTokenData;
import com.jio.devicetracker.database.pojo.request.GenerateTokenRequest;
import com.jio.devicetracker.database.pojo.response.GenerateTokenResponse;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

/**
 * Signup Mobile Number Fragment
 */
public class SignupMobileNumberFragment extends Fragment implements View.OnClickListener {

    private EditText signUpNumberEditText;
    private Button continueNumberSignup;
    private TextView signupMobileNumberErrorCode;
    private TextView phoneNumberTextView;
    private String name;
    private String phoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_mobile_number, container, false);
        setLayoutData(view);
        changeButtonColorOnDataEntry();
        return view;
    }

    private void setLayoutData(View view) {
        TextView signUpMobileNumberTextView = view.findViewById(R.id.signUpMobileNumberTextView);
        signUpMobileNumberTextView.setTypeface(Util.mTypeface(getActivity(), 5));
        TextView enterSignupMobileNumber = view.findViewById(R.id.enterSignupMobileNumber);
        enterSignupMobileNumber.setTypeface(Util.mTypeface(getActivity(), 3));
        signUpNumberEditText = view.findViewById(R.id.signUpNumberEditText);
        signUpNumberEditText.setTypeface(Util.mTypeface(getActivity(), 5));
        continueNumberSignup = view.findViewById(R.id.continueNumberSignup);
        continueNumberSignup.setTypeface(Util.mTypeface(getActivity(), 5));
        continueNumberSignup.setOnClickListener(this);
        signupMobileNumberErrorCode = view.findViewById(R.id.signupMobileNumberErrorCode);
        signupMobileNumberErrorCode.setTypeface(Util.mTypeface(getActivity(), 5));
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView);
        name = getArguments().getString(Constant.NAME);
    }

    private void changeButtonColorOnDataEntry() {
        signUpNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                continueNumberSignup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                continueNumberSignup.setTextColor(Color.WHITE);
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.black));
                signUpNumberEditText.setBackgroundTintList(colorStateList);
                signupMobileNumberErrorCode.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = signUpNumberEditText.getText().toString().trim();
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.black));
                signUpNumberEditText.setBackgroundTintList(colorStateList);
                signupMobileNumberErrorCode.setVisibility(View.INVISIBLE);
                if (! Constant.EMPTY_STRING.equalsIgnoreCase(phoneNumber)) {
                    phoneNumberTextView.setVisibility(View.VISIBLE);
                    continueNumberSignup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                    continueNumberSignup.setTextColor(Color.WHITE);
                } else if (Constant.EMPTY_STRING.equalsIgnoreCase(phoneNumber)) {
                    continueNumberSignup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector, null));
                    phoneNumberTextView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continueNumberSignup) {
            phoneNumber = signUpNumberEditText.getText().toString().trim();
            if (!Util.isValidMobileNumber(phoneNumber)) {
                signupMobileNumberErrorCode.setVisibility(View.VISIBLE);
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.errorColor));
                signUpNumberEditText.setBackgroundTintList(colorStateList);
                return;
            }
            makeSafetyNetCall();
        }
    }

    private void makeSafetyNetCall() {
        SafetyNet.getClient(getActivity()).verifyWithRecaptcha(Constant.GOOGLE_RECAPCHA_KEY)
                .addOnSuccessListener(getActivity(), response -> {
                    if (!response.getTokenResult().isEmpty()) {
                        Util.getInstance().setExpiryTime();
                        Util.getInstance().updateGoogleToken(response.getTokenResult());
                        generateRegistrationTokenAPICall(phoneNumber);
                    }
                })
                .addOnFailureListener(getActivity(), e -> Toast.makeText(getActivity(), Constant.GOOGLE_RECAPTCHA_ERROR, Toast.LENGTH_SHORT).show());
    }

    /**
     * Used to generate registration Token
     */
    private void generateRegistrationTokenAPICall(String phoneNumber) {
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
                //Toast.makeText(getActivity(), Constant.GENERATE_TOKEN_SUCCESS, Toast.LENGTH_SHORT).show();
                // Switch to the Signup OTP Fragment
                SignupOTPFragment signupOTPFragment = new SignupOTPFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.NAME, name);
                bundle.putString(Constant.MOBILE_NUMBER, phoneNumber);
                signupOTPFragment.setArguments(bundle);
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                trans.replace(R.id.signupFrameLayout, signupOTPFragment);
                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                trans.addToBackStack(null);
                trans.commit();
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
                Util.alertDilogBox(Constant.REGISTRAION_ALERT_409, Constant.ALERT_TITLE, getActivity());
                return;
            }
        }
    }

}
