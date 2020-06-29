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
import com.jio.devicetracker.database.pojo.GenerateLoginTokenData;
import com.jio.devicetracker.database.pojo.request.GenerateLoginTokenRequest;
import com.jio.devicetracker.database.pojo.response.GenerateTokenResponse;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.menu.TermAndConditionPolicyActivity;

import java.util.Objects;

/**
 * Login fragment
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText mobileNumberEditText;
    private TextView mobileNumberTextView;
    private String phoneNumber;
    private TextView mobileNumberErrorCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        setLayoutData(view);
        showMobileNumberTextViewOnDataEntry();
        return view;
    }

    private void setLayoutData(View view) {
        TextView signInHelloTextView = view.findViewById(R.id.signInHelloTextView);
        signInHelloTextView.setTypeface(Util.mTypeface(getActivity(), 5));
        TextView enterMobileNumberTextView = view.findViewById(R.id.enterMobileNumberTextView);
        enterMobileNumberTextView.setTypeface(Util.mTypeface(getActivity(), 3));
        mobileNumberEditText = view.findViewById(R.id.mobileNumberEditText);
        mobileNumberEditText.setTypeface(Util.mTypeface(getActivity(), 5));
        Button continueButton = view.findViewById(R.id.continueLogin);
        continueButton.setTypeface(Util.mTypeface(getActivity(), 5));
        continueButton.setOnClickListener(this);
        TextView requestOTPTextView = view.findViewById(R.id.requestOTPTextView);
        requestOTPTextView.setTypeface(Util.mTypeface(getActivity(), 3));
        TextView termConditionTextView = view.findViewById(R.id.termConditionTextView);
        termConditionTextView.setTypeface(Util.mTypeface(getActivity(), 5));
        termConditionTextView.setOnClickListener(this);
        mobileNumberTextView = view.findViewById(R.id.mobileNumberTextView);
        mobileNumberTextView.setTypeface(Util.mTypeface(getActivity(), 5));
        mobileNumberErrorCode = view.findViewById(R.id.mobileNumberErrorCode);
        mobileNumberEditText.setTypeface(Util.mTypeface(getActivity(), 5));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continueLogin) {
            phoneNumber = mobileNumberEditText.getText().toString().trim();
            makeSafetyNetCall();
        } else {
            Intent intent = new Intent(getContext(), TermAndConditionPolicyActivity.class);
            startActivity(intent);
        }
    }

    private void showMobileNumberTextViewOnDataEntry() {
        mobileNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mobileNumberTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = mobileNumberEditText.getText().toString();
                if (!Constant.EMPTY_STRING.equalsIgnoreCase(name)) {
                    mobileNumberTextView.setVisibility(View.VISIBLE);
                } else if (Constant.EMPTY_STRING.equalsIgnoreCase(name)) {
                    mobileNumberTextView.setVisibility(View.INVISIBLE);
                    mobileNumberErrorCode.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * Make an api call to generate Login token
     */
    private void generateLoginTokenAPICall() {
        if (!Util.isValidMobileNumber(phoneNumber)) {
            mobileNumberErrorCode.setVisibility(View.VISIBLE);
            return;
        }
        GenerateLoginTokenData generateLoginTokenData = new GenerateLoginTokenData();
        GenerateLoginTokenData.Role role = new GenerateLoginTokenData().new Role();
        role.setCode(Constant.SUPERVISOR);
        generateLoginTokenData.setPhone(phoneNumber);
        generateLoginTokenData.setRole(role);
        Util.getInstance().showProgressBarDialog(getActivity());
        RequestHandler.getInstance(getActivity()).handleRequest(new GenerateLoginTokenRequest(new GenerateLoginTokenSuccessListener(), new GenerateLoginTokenErrorListener(), generateLoginTokenData));
    }

    private void makeSafetyNetCall() {
        SafetyNet.getClient(Objects.requireNonNull(getActivity())).verifyWithRecaptcha(Constant.GOOGLE_RECAPCHA_KEY)
                .addOnSuccessListener(getActivity(), response -> {
                    if (!response.getTokenResult().isEmpty()) {
                        Util.getInstance().setExpiryTime();
                        Util.getInstance().updateGoogleToken(response.getTokenResult());
                        generateLoginTokenAPICall();
                    }
                })
                .addOnFailureListener(getActivity(), e -> Toast.makeText(getActivity(), Constant.GOOGLE_RECAPTCHA_ERROR, Toast.LENGTH_SHORT).show());
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
                OTPEntryFragment otpEntryFragment = new OTPEntryFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.MOBILE_NUMBER, phoneNumber);
                otpEntryFragment.setArguments(bundle);
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                trans.replace(R.id.loginFrameLayout, otpEntryFragment);
                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                trans.addToBackStack(null);
                trans.commit();
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
                mobileNumberEditText.setError(Constant.GENERATE_TOKEN_FAILURE);
            }
        }
    }
}