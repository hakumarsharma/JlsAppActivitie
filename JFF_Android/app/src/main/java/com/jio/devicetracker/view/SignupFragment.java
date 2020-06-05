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
 * Signup fragment
 */
public class SignupFragment extends Fragment implements View.OnClickListener {

    private EditText signUpNameEditText;
    private Button continueSignup;
    private TextView nameTextView;
    private TextView wrongNameErrorTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        setLayoutData(view);
        changeButtonColorOnDataEntry();
        return view;
    }

    private void setLayoutData(View view) {
        TextView signUpcustomizeProfile = view.findViewById(R.id.signUpcustomizeProfile);
        signUpcustomizeProfile.setTypeface(Util.mTypeface(getActivity(), 5));
        TextView enterNameTextView = view.findViewById(R.id.enterNameTextView);
        enterNameTextView.setTypeface(Util.mTypeface(getActivity(), 3));
        signUpNameEditText = view.findViewById(R.id.signUpNameEditText);
        signUpNameEditText.setTypeface(Util.mTypeface(getActivity(), 5));
        continueSignup = view.findViewById(R.id.continueSignup);
        continueSignup.setTypeface(Util.mTypeface(getActivity(), 5));
        continueSignup.setOnClickListener(this);
        TextView signupRequestOTPTextView = view.findViewById(R.id.signupRequestOTPTextView);
        signupRequestOTPTextView.setTypeface(Util.mTypeface(getActivity(), 3));
        TextView signupTermConditionTextView = view.findViewById(R.id.signupTermConditionTextView);
        signupTermConditionTextView.setTypeface(Util.mTypeface(getActivity(), 5));
        nameTextView = view.findViewById(R.id.nameTextView);
        nameTextView.setTypeface(Util.mTypeface(getActivity(), 5));
        wrongNameErrorTextView = view.findViewById(R.id.wrongNameErrorTextView);
        wrongNameErrorTextView.setTypeface(Util.mTypeface(getActivity(), 5));
    }

    private void changeButtonColorOnDataEntry() {
        signUpNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                continueSignup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                continueSignup.setTextColor(Color.WHITE);
                nameTextView.setVisibility(View.VISIBLE);
                wrongNameErrorTextView.setVisibility(View.INVISIBLE);
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.black));
                signUpNameEditText.setBackgroundTintList(colorStateList);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = signUpNameEditText.getText().toString();
                if (!Constant.EMPTY_STRING.equalsIgnoreCase(name)) {
                    continueSignup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                    continueSignup.setTextColor(Color.WHITE);
                    nameTextView.setVisibility(View.VISIBLE);
                    wrongNameErrorTextView.setVisibility(View.INVISIBLE);
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.black));
                    signUpNameEditText.setBackgroundTintList(colorStateList);
                } else if (Constant.EMPTY_STRING.equalsIgnoreCase(name)) {
                    nameTextView.setVisibility(View.INVISIBLE);
                    continueSignup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector, null));
                    continueSignup.setTextColor(Color.WHITE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continueSignup) {
            String name = signUpNameEditText.getText().toString().trim();
            if(name.equalsIgnoreCase(Constant.EMPTY_STRING)) {
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.errorColor));
                signUpNameEditText.setBackgroundTintList(colorStateList);
                wrongNameErrorTextView.setVisibility(View.VISIBLE);
                return;
            }
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            SignupEmailFragment signupEmailFragment = new SignupEmailFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.NAME, name);
            signupEmailFragment.setArguments(bundle);
            trans.replace(R.id.signupFrameLayout, signupEmailFragment);
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            trans.addToBackStack(null);
            trans.commit();
        }
    }
}