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

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

/**
 * Signup Mobile Number Fragment
 */
public class SignupMobileNumberFragment extends Fragment implements View.OnClickListener {

    private EditText signUpNumberEditText;
    private Button continueNumberSignup;
    private TextView signupMobileNumberErrorCode;

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
                continueNumberSignup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                continueNumberSignup.setTextColor(Color.WHITE);
                if (Constant.EMPTY_STRING.equalsIgnoreCase(signUpNumberEditText.getText().toString().trim())) {
                    continueNumberSignup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector, null));
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.black));
                    signUpNumberEditText.setBackgroundTintList(colorStateList);
                    signupMobileNumberErrorCode.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continueNumberSignup) {
            if (!Util.isValidMobileNumber(signUpNumberEditText.getText().toString().trim())) {
                signupMobileNumberErrorCode.setVisibility(View.VISIBLE);
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.errorColor));
                signUpNumberEditText.setBackgroundTintList(colorStateList);
                return;
            }
        }
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.replace(R.id.signup_root_frame, new SignupEmailFragment());
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);
        trans.commit();
    }
}
