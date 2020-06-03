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
import android.widget.TextView;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.jio.devicetracker.R;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        phoneNumber = getArguments().getString(Constant.MOBILE_NUMBER);
        MessageListener messageListener = new OTPEntryFragment();
        MessageReceiver.bindListener(messageListener);
        View view = inflater.inflate(R.layout.fragment_otp_entry, container, false);
        setLayoutData(view);
        return view;
    }

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
    }

    @Override
    public void onClick(View v) {
        // Todo
    }

    @Override
    public void messageReceived(String message, String phoneNum) {
        if (message.contains(Constant.OTP_MESSAGE) && pinEntryEditText != null) {
            pinEntryEditText.setText(message.substring(message.indexOf("OTP") + 6, message.indexOf("to") - 1));
        }
    }
}
