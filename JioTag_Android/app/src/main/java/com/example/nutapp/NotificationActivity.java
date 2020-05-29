package com.example.nutapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.nutapp.util.JioConstant;

public class NotificationActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_notification);
        setToolbarIconTitle();
    }

    public void setToolbarIconTitle()
    {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(JioConstant.NOTIFICATION_MODE_TITLE);
        title.setTypeface(JioUtils.mTypeface(this,5));
        Button backIcn = findViewById(R.id.back);
        backIcn.setVisibility(View.VISIBLE);
        backIcn.setOnClickListener(this);
        Button homeicn = findViewById(R.id.home);
        homeicn.setVisibility(View.VISIBLE);
        homeicn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.back:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.home:
                Intent intentHome = new Intent();
                setResult(JioUtils.HOME_KEY, intentHome);
                finish();
                break;

            default:
                break;
        }
    }
}
