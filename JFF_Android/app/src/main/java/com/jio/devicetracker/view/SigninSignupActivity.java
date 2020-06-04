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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.SigninSignupAdapter;

public class SigninSignupActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager signinSignupviewPager;
    private SigninSignupAdapter signinSignupAdapter;
//    private TabItem loginScreenTab;
//    private TabItem signupScreenTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        setLayoutData();
    }

    private void setLayoutData() {
        Toolbar toolbar = findViewById(R.id.signInSignUpToolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setTypeface(Util.mTypeface(this, 5));
        toolbarTitle.setText(Constant.WELCOME);

        tabLayout = findViewById(R.id.signinSignupTablayout);
        signinSignupAdapter = new SigninSignupAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        signinSignupviewPager = findViewById(R.id.signinSignupviewPager);
        signinSignupviewPager.setAdapter(signinSignupAdapter);
        signinSignupviewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        onTabClicked();
    }

    private void onTabClicked() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                signinSignupviewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    tabLayout.getTabAt(0).setText(Constant.LOGIN_WITH_DOT);
                } else if (tab.getPosition() == 1) {
                    tabLayout.getTabAt(1).setText(Constant.SIGNUP_WITH_DOT);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    tabLayout.getTabAt(0).setText(Constant.LOGIN_WITHOUT_DOT);
                } else if (tab.getPosition() == 1) {
                    tabLayout.getTabAt(1).setText(Constant.SIGNUP_WITHOUT_DOT);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
