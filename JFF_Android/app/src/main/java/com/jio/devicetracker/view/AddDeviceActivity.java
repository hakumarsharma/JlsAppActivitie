package com.jio.devicetracker.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;

public class AddDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddevice);
        toolbar = findViewById(R.id.adddDeviceToolbar);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.Add_Device);
        toolbar.setBackgroundColor(getResources().getColor(R.color.cardviewlayout_device_background_color));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_device) {

        } else if (v.getId() == R.id.add_device_Later){

        }
    }
}

