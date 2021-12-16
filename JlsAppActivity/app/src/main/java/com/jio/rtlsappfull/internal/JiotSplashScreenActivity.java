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
            if(checkSharedPreferences()) {
                Intent i = new Intent(JiotSplashScreenActivity.this, JioPermissions.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            } else {
                Intent i = new Intent(JiotSplashScreenActivity.this, AboutAppActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        }, JiotUtils.SPLASH_TIME_OUT);
    }

    private boolean checkSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String aboutApp = sharedPreferences.getString("about_app", null);
        if(aboutApp != null && aboutApp.equalsIgnoreCase("yes")) {
            return true;
        } else {
            SharedPreferences.Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
            editor.putString("about_app", "yes");
            editor.commit();
            return false;
        }
    }

}

