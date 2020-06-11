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
import com.jio.devicetracker.view.menu.TermAndConditionPolicyActivity;

/**
 * Signup email id fragment
 */
public class SignupEmailFragment extends Fragment implements View.OnClickListener {

    private EditText signUpEmailEditText;
    private Button continueEmailSignup;
    private TextView emailTextView;
    private TextView signupAddLaterTextView;
    private TextView wrongEmailErrorTextView;

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
        emailTermConditionTextView.setOnClickListener(this);
        signupAddLaterTextView = view.findViewById(R.id.signupAddLaterTextView);
        signupAddLaterTextView.setTypeface(Util.mTypeface(getActivity(), 5));
        signupAddLaterTextView.setOnClickListener(this);
        wrongEmailErrorTextView = view.findViewById(R.id.wrongEmailErrorTextView);
        changeButtonColorOnDataEntry();
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
                emailTextView.setVisibility(View.VISIBLE);
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.black));
                signUpEmailEditText.setBackgroundTintList(colorStateList);
                wrongEmailErrorTextView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = signUpEmailEditText.getText().toString().trim();
                if (!Constant.EMPTY_STRING.equalsIgnoreCase(email)) {
                    continueEmailSignup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                    continueEmailSignup.setTextColor(Color.WHITE);
                    wrongEmailErrorTextView.setVisibility(View.INVISIBLE);
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.black));
                    signUpEmailEditText.setBackgroundTintList(colorStateList);
                } else if (Constant.EMPTY_STRING.equalsIgnoreCase(email)) {
                    emailTextView.setVisibility(View.INVISIBLE);
                    continueEmailSignup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector, null));
                    continueEmailSignup.setTextColor(Color.WHITE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continueEmailSignup) {
            if(signUpEmailEditText.getText().toString().trim().equalsIgnoreCase(Constant.EMPTY_STRING)) {
                wrongEmailErrorTextView.setVisibility(View.VISIBLE);
                return;
            }
        } else if (v.getId() == R.id.signupAddLaterTextView) {
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            SignupMobileNumberFragment signupMobileNumberFragment = new SignupMobileNumberFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.NAME, getArguments().getString(Constant.NAME));
            signupMobileNumberFragment.setArguments(bundle);
            trans.replace(R.id.signupFrameLayout, signupMobileNumberFragment);
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            trans.addToBackStack(null);
            trans.commit();
        }else if (v.getId() == R.id.emailTermConditionTextView){
            Intent intent = new Intent(getContext(), TermAndConditionPolicyActivity.class);
            startActivity(intent);
        }
    }
}
