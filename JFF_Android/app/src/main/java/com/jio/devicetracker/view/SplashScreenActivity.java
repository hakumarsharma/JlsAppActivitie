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

import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;

import com.jio.devicetracker.R;

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
        mWaitHandler.postDelayed(() -> {
            try {
                gotoScreen();
                finish();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 4000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWaitHandler.removeCallbacksAndMessages(null);
    }

    /**
     * According to the condition it navigates to the particular screen
     */
    public void gotoScreen() {
        boolean flag = Util.getTermconditionFlag(this);
        boolean flagAutologin = Util.getAutologinStatus(this);
        boolean flagLocation = Util.getLocationFlagStatus(this);
        if (!flag) {
            Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(intent);
        } else if (flagAutologin) {
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
        } else if (flagLocation) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
        } else {
//            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(intent);
            Intent intent = new Intent(getApplicationContext(), AddPeopleActivity.class);
            startActivity(intent);
        }

    }
}
