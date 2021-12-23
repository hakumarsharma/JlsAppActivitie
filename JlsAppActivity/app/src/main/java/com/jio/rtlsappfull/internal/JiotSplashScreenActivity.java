package com.jio.rtlsappfull.internal;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.utils.JiotUtils;

public class JiotSplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(() -> {
            if (checkAboutApp() && checkRtlsKey()) {
                Intent i = new Intent(JiotSplashScreenActivity.this, JiotMainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            } else if (!checkAboutApp()) {
                Intent i = new Intent(JiotSplashScreenActivity.this, AboutAppActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            } else if (!checkRtlsKey()) {
                Intent i = new Intent(JiotSplashScreenActivity.this, JiotUserName.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        }, JiotUtils.SPLASH_TIME_OUT);
    }

    private boolean checkRtlsKey() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String fetchRtlsKey = sharedPreferences.getString("fetch_rtls_key", null);
        if (fetchRtlsKey != null && fetchRtlsKey.equalsIgnoreCase("success"))
            return true;
        return false;
    }

    private boolean checkAboutApp() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String aboutApp = sharedPreferences.getString("about_app", null);
        if (aboutApp != null && aboutApp.equalsIgnoreCase("yes"))
            return true;
        return false;
    }
}

