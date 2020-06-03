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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Util;

/**
 * Signup Mobile Number Fragment
 */
public class SignupMobileNumberFragment extends Fragment implements View.OnClickListener{

    private EditText signUpNumberEditText;
    private Button continueNumberSignup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_mobile_number, container, false);
        setLayoutData(view);
        return view;
    }

    private void setLayoutData(View view) {
        TextView alternateMobileNumberTextView = view.findViewById(R.id.alternateMobileNumberTextView);
        alternateMobileNumberTextView.setTypeface(Util.mTypeface(getActivity(), 5));
        TextView enterAlternateNumber = view.findViewById(R.id.enterAlternateNumber);
        enterAlternateNumber.setTypeface(Util.mTypeface(getActivity(), 3));
        signUpNumberEditText = view.findViewById(R.id.signUpNumberEditText);
        signUpNumberEditText.setTypeface(Util.mTypeface(getActivity(), 5));
        continueNumberSignup = view.findViewById(R.id.continueNumberSignup);
        continueNumberSignup.setTypeface(Util.mTypeface(getActivity(), 5));
        continueNumberSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.continueNumberSignup) {
            // Todo
        }
    }
}
