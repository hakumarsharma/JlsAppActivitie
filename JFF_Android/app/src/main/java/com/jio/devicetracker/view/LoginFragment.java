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
 * Login fragment
 */
public class LoginFragment extends Fragment {

    private EditText mobileNumberEditText;
    private Button continueButton;
    private TextView mobileNumberTextView;

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
        signInHelloTextView.setTypeface(Util.mTypeface(getActivity(),5));
        TextView enterMobileNumberTextView = view.findViewById(R.id.enterMobileNumberTextView);
        enterMobileNumberTextView.setTypeface(Util.mTypeface(getActivity(),3));
        mobileNumberEditText = view.findViewById(R.id.mobileNumberEditText);
        mobileNumberEditText.setTypeface(Util.mTypeface(getActivity(),5));
        continueButton = view.findViewById(R.id.continueLogin);
        continueButton.setTypeface(Util.mTypeface(getActivity(),5));
        TextView requestOTPTextView = view.findViewById(R.id.requestOTPTextView);
        requestOTPTextView.setTypeface(Util.mTypeface(getActivity(),3));
        TextView termConditionTextView = view.findViewById(R.id.termConditionTextView);
        termConditionTextView.setTypeface(Util.mTypeface(getActivity(),5));
        mobileNumberTextView = view.findViewById(R.id.mobileNumberTextView);
        mobileNumberTextView.setTypeface(Util.mTypeface(getActivity(), 5));
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
                } else if(Constant.EMPTY_STRING.equalsIgnoreCase(name)) {
                    mobileNumberTextView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}