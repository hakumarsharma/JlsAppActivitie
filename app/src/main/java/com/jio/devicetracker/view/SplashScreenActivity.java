// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import com.jio.devicetracker.util.Util;

import com.jio.devicetracker.R;

/**
 * Implementation of Splash Screen.This class creates splash screen for JFF application
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
                /*Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);*/
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

    public void gotoScreen()
    {
        boolean flag = Util.getTermconditionFlag(SplashScreenActivity.this);
        if(flag){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(intent);
        }

    }
}
