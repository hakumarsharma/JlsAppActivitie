package com.jio.devicetracker.view.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

public class AttachDeviceActivity extends Activity implements View.OnClickListener {
    private EditText deviceNumber;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_device);
        TextView title = findViewById(R.id.toolbar_title);
        TextView deviceTitle = findViewById(R.id.device_number);
        title.setText(Constant.ATTACH_DEVICE_TITLE);
        title.setTypeface(Util.mTypeface(this,5));
        deviceNumber = findViewById(R.id.device_edit_name);
        Button connectBtn = findViewById(R.id.connect_btn);
        connectBtn.setOnClickListener(this);

        deviceNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
              if(!s.toString().isEmpty()){
                  connectBtn.setBackground(getResources().getDrawable(R.drawable.button_frame_blue));
                  deviceTitle.setVisibility(View.VISIBLE);
              } else {
                  deviceTitle.setVisibility(View.INVISIBLE);
              }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(!deviceNumber.getText().toString().isEmpty() &&Util.isValidMobileNumber(deviceNumber.getText().toString())){
            Intent intent = new Intent(this,DeviceNameActivity.class);
            intent.putExtra("DeviceNumber",deviceNumber.getText().toString());
            startActivity(intent);
        } else {
            deviceNumber.setError(Constant.VALID_PHONE_NUMBER);
            return;
        }

    }
}
