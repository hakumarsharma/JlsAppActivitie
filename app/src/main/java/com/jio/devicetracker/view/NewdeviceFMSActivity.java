// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;

public class NewdeviceFMSActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device_fms);

        TextView title = findViewById(R.id.toolbar_title);
        title.setText("Add");



    }

    @Override
    public void onClick(View v) {

    }
}
