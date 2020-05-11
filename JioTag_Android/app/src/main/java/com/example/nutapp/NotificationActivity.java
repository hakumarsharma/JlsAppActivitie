package com.example.nutapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.nutapp.util.JioConstant;

public class NotificationActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_notification);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(JioConstant.NOTIFICATION_MODE_TITLE);
    }
}
