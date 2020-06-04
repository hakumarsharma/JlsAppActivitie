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

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

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
 * Signup email id fragment
 */
public class SignupEmailFragment extends Fragment implements View.OnClickListener {

    private EditText signUpEmailEditText;
    private Button continueEmailSignup;
    private TextView emailTextView;
    private TextView signupAddLaterTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_email, container, false);
        setLayoutData(view);
        return view;
    }

    private void setLayoutData(View view) {
        TextView signUpEmailProfile = view.findViewById(R.id.signUpEmailProfile);
        signUpEmailProfile.setTypeface(Util.mTypeface(getActivity(), 5));
        TextView enterEmailTextView = view.findViewById(R.id.enterEmailTextView);
        enterEmailTextView.setTypeface(Util.mTypeface(getActivity(), 3));
        emailTextView = view.findViewById(R.id.emailTextView);
        emailTextView.setTypeface(Util.mTypeface(getActivity(), 5));
        signUpEmailEditText = view.findViewById(R.id.signUpEmailEditText);
        signUpEmailEditText.setTypeface(Util.mTypeface(getActivity(), 5));
        continueEmailSignup = view.findViewById(R.id.continueEmailSignup);
        continueEmailSignup.setTypeface(Util.mTypeface(getActivity(), 5));
        continueEmailSignup.setOnClickListener(this);
        TextView emailRequestOTPTextView = view.findViewById(R.id.emailRequestOTPTextView);
        emailRequestOTPTextView.setTypeface(Util.mTypeface(getActivity(), 3));
        TextView emailTermConditionTextView = view.findViewById(R.id.emailTermConditionTextView);
        emailTermConditionTextView.setTypeface(Util.mTypeface(getActivity(), 5));
        signupAddLaterTextView = view.findViewById(R.id.signupAddLaterTextView);
        signupAddLaterTextView.setTypeface(Util.mTypeface(getActivity(), 5));
    }

    private void changeButtonColorOnDataEntry() {
        signUpEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                continueEmailSignup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                continueEmailSignup.setTextColor(Color.WHITE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                continueEmailSignup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                continueEmailSignup.setTextColor(Color.WHITE);
                if (Constant.EMPTY_STRING.equalsIgnoreCase(signUpEmailEditText.getText().toString().trim())) {
                    continueEmailSignup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector, null));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.continueEmailSignup) {
            // Todo
        } else if(v.getId() == R.id.signupAddLaterTextView) {
            // Todo
        }
    }
}
