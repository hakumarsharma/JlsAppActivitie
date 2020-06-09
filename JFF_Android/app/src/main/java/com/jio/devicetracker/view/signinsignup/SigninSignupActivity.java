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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionManager;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.SigninSignupAdapter;

public class SigninSignupActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager signinSignupviewPager;
    private SigninSignupAdapter signinSignupAdapter;
    private static final int PERMIT_ALL = 1;
    private TextView signinTextView;
    private TextView signupTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        requestPermission();
        setLayoutData();
    }

    private void setLayoutData() {
        Toolbar toolbar = findViewById(R.id.signInSignUpToolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setTypeface(Util.mTypeface(this, 5));
        toolbarTitle.setText(Constant.WELCOME);
        signinSignupAdapter = new SigninSignupAdapter(getSupportFragmentManager(), 2);
        signinSignupviewPager = findViewById(R.id.signinSignupviewPager);
        signinSignupviewPager.setAdapter(signinSignupAdapter);
        signinTextView = findViewById(R.id.signinTextView);
        signupTextView = findViewById(R.id.signupTextView);
        signinTextView.setOnClickListener(this);
        signupTextView.setOnClickListener(this);
        signinTextView.setTypeface(Util.mTypeface(this, 5));
        signupTextView.setTypeface(Util.mTypeface(this, 5));
        signinTextView.setText(Constant.LOGIN_WITHOUT_DOT + Html.fromHtml(getResources().getString(R.string.white_indicater)));
        Util.getInstance().setTermconditionFlag(this, true);
        onTabClicked();
    }

    private void onTabClicked() {
        signinSignupviewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // To do
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    signinTextView.setText(Constant.LOGIN_WITHOUT_DOT + Html.fromHtml(getResources().getString(R.string.white_indicater)));
                    signinTextView.setTextColor(Color.WHITE);
                    signupTextView.setText(Constant.SIGNUP_WITHOUT_DOT);
                    signupTextView.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                } else if(position == 1){
                    signupTextView.setText(Constant.SIGNUP_WITHOUT_DOT + Html.fromHtml(getResources().getString(R.string.white_indicater)));
                    signupTextView.setTextColor(Color.WHITE);
                    signinTextView.setText(Constant.LOGIN_WITHOUT_DOT);
                    signinTextView.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // To do
            }
        });
    }

    // Request for SMS and Phone Permissions
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_CONTACTS}, PERMIT_ALL);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (PERMIT_ALL == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        switch (requestCode) {
            case PERMIT_ALL: {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // Todo
                }
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    // Todo
                }
                // If request is cancelled, the result arrays are empty.
                for (int grantResult : grantResults) {
                    if (grantResults.length > 0 && grantResult == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permission is granted");
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signinTextView) {
            signinSignupviewPager.setCurrentItem(0);
            signinTextView.setText(Constant.LOGIN_WITHOUT_DOT + Html.fromHtml(getResources().getString(R.string.white_indicater)));
            signinTextView.setTextColor(Color.WHITE);
            signupTextView.setText(Constant.SIGNUP_WITHOUT_DOT);
            signupTextView.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
        } else if (v.getId() == R.id.signupTextView) {
            signinSignupviewPager.setCurrentItem(1);
            signupTextView.setText(Constant.SIGNUP_WITHOUT_DOT + Html.fromHtml(getResources().getString(R.string.white_indicater)));
            signupTextView.setTextColor(Color.WHITE);
            signinTextView.setText(Constant.LOGIN_WITHOUT_DOT);
            signinTextView.setTextColor(getResources().getColor(R.color.tabBarUnselectedColor));
        }
    }
}
