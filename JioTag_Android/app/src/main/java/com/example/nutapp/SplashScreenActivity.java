package com.example.nutapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    SharedPreferences preferences;
    public static boolean m_close_spalsh=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        TextView splashTextView=(TextView)findViewById(R.id.splash_text_view);
        splashTextView.setTypeface(JioUtils.mTypeface(this, 5));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                preferences = getSharedPreferences(JioUtils.MYPREFERENCES, Context.MODE_PRIVATE);
                String firstBoot=preferences.getString("FIRSTBOOT", "true");
                Intent i;
                if(firstBoot.toString().trim().equalsIgnoreCase("true")) {
                    i = new Intent(SplashScreenActivity.this, OtpRequest.class);
                }else{
                    i = new Intent(SplashScreenActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    m_close_spalsh=true;
                }
                startActivity(i);
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
