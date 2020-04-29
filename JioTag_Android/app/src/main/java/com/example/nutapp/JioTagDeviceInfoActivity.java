package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nutapp.util.Constant;

public class JioTagDeviceInfoActivity extends AppCompatActivity {
    TextView deviceName;
    TextView deviceNumber;
    TextView deviceImei;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_from_qrcode);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.DEVICE_INFO_TITLE);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        title.setTypeface(JioUtils.mTypeface(this, 5));
        deviceName = findViewById(R.id.jiotag_device_name);
        deviceNumber = findViewById(R.id.jiotag_device_number);
        deviceImei = findViewById(R.id.jiotag_device_imei);
        Intent intent = getIntent();
        String qrValue = intent.getStringExtra("QRCodeValue");
        setNameNumberImei(qrValue);

    }
    private void setNameNumberImei(String qrValue) {
        if(qrValue != null){
            String []splitString = qrValue.split("\n");
            String name = splitString[0];
            String number = splitString[1];
            String imeiNumber = splitString[2];
            deviceName.setText(name);
            deviceNumber.setText(number);
            deviceImei.setText(imeiNumber);

        }

    }
}
