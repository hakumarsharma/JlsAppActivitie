package com.example.nutapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.nutapp.util.JioConstant;

public class SlientModeActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_silent_mode);
        setToolbarIconTitle();
    }

    public void setToolbarIconTitle()
    {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(JioConstant.SLIENT_MODE_TITLE);
        title.setTypeface(JioUtils.mTypeface(this,5));
        Button backIcn = findViewById(R.id.back);
        backIcn.setVisibility(View.VISIBLE);
        Button homeicn = findViewById(R.id.home);
        homeicn.setVisibility(View.VISIBLE);
    }
}
