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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import com.jio.devicetracker.util.Util;

import com.jio.devicetracker.R;
import com.jio.devicetracker.view.dashboard.DashboardMainActivity;
import com.jio.devicetracker.view.geofence.GeofenceActivity;
import com.jio.devicetracker.view.menu.HelpActivity;
import com.jio.devicetracker.view.signinsignup.SigninSignupActivity;


/**
 * Implementation of Splash Screen.This class creates splash screen for People tracker application
 */
public class SplashScreenActivity extends AppCompatActivity {

    private Handler mWaitHandler = new Handler();
    public Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        displaySplashScreen();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWaitHandler.removeCallbacksAndMessages(null);
    }


    private void displaySplashScreen() {
        mWaitHandler.postDelayed(() -> {
            try {
                gotoScreen();
                finish();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 4000);
    }

    /**
     * According to the condition it navigates to the particular screen
     */
    public void gotoScreen() {
        boolean flag = Util.getInstance().getTermconditionFlag(this);
        boolean flagAutologin = Util.getInstance().getAutologinStatus(this);
        if (!flag) {
            Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(intent);
        } else if (flagAutologin) {
            Intent intent = new Intent(getApplicationContext(), DashboardMainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), SigninSignupActivity.class);
            startActivity(intent);
        }
    }
}
