// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class SplashScreenActivity extends AppCompatActivity {

    private Handler mWaitHandler = new Handler();
    public Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mWaitHandler.postDelayed(() -> {
            try {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
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
}
