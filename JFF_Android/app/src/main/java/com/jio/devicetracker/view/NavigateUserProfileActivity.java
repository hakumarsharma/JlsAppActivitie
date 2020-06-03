package com.jio.devicetracker.view;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;

public class NavigateUserProfileActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
    }
}
