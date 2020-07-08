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
    private TextView errorText;
    private View underLine;
    private EditText deviceImei;
    private TextView imeiErrorText;
    private View imeiUnderLine;
    private static String groupId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_device);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.ATTACH_DEVICE_TITLE);
        title.setTypeface(Util.mTypeface(this,5));
        errorText = findViewById(R.id.number_validation_text);
        deviceNumber = findViewById(R.id.device_edit_name);
        underLine = findViewById(R.id.number_edit_line);

        imeiErrorText = findViewById(R.id.imei_validation_text);
        deviceImei = findViewById(R.id.device_edit_imei);
        imeiUnderLine = findViewById(R.id.imei_edit_line);


        Intent intent = getIntent();
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        editTextCallBackMethods();

    }

    private void editTextCallBackMethods(){
        Button connectBtn = findViewById(R.id.connect_btn);
        connectBtn.setOnClickListener(this);

        TextView deviceTitle = findViewById(R.id.device_number);
        TextView deviceImeiTitle = findViewById(R.id.device_imei);


        deviceNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Todo
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Todo
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    deviceTitle.setVisibility(View.VISIBLE);
                    if (deviceImei != null && !deviceImei.getText().toString().isEmpty()){
                        connectBtn.setBackground(getResources().getDrawable(R.drawable.button_frame_blue));
                    }
                } else {
                    deviceTitle.setVisibility(View.INVISIBLE);
                }
            }
        });

        deviceImei.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Todo
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Todo
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    deviceImeiTitle.setVisibility(View.VISIBLE);
                    if (deviceImei != null && !deviceImei.getText().toString().isEmpty()){
                        connectBtn.setBackground(getResources().getDrawable(R.drawable.button_frame_blue));
                    }
                } else {
                    deviceImeiTitle.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    @Override
    public void onClick(View v) {

        if((deviceNumber.getText().toString().isEmpty() || !Util.isValidMobileNumberForPet(deviceNumber.getText().toString())) && (deviceImei.getText().toString().isEmpty() || !Util.isValidIMEINumber(deviceImei.getText().toString()))){
            errorText.setVisibility(View.VISIBLE);
            underLine.setBackgroundColor(getResources().getColor(R.color.errorColor));
            imeiErrorText.setVisibility(View.VISIBLE);
            imeiUnderLine.setBackgroundColor(getResources().getColor(R.color.errorColor));
            return;
        }

        if(deviceNumber.getText().toString().isEmpty() || !Util.isValidMobileNumberForPet(deviceNumber.getText().toString())){
            errorText.setVisibility(View.VISIBLE);
            underLine.setBackgroundColor(getResources().getColor(R.color.errorColor));
            imeiErrorText.setVisibility(View.INVISIBLE);
            imeiUnderLine.setBackgroundColor(getResources().getColor(R.color.timerColor));
            return;
        }

        if(deviceImei.getText().toString().isEmpty() || !Util.isValidIMEINumber(deviceImei.getText().toString())){
            errorText.setVisibility(View.INVISIBLE);
            underLine.setBackgroundColor(getResources().getColor(R.color.timerColor));
            imeiErrorText.setVisibility(View.VISIBLE);
            imeiUnderLine.setBackgroundColor(getResources().getColor(R.color.errorColor));
            return;
        }

        errorText.setVisibility(View.INVISIBLE);
        underLine.setBackgroundColor(getResources().getColor(R.color.timerColor));
        imeiErrorText.setVisibility(View.INVISIBLE);
        imeiUnderLine.setBackgroundColor(getResources().getColor(R.color.timerColor));

        Intent intent = new Intent(this,DeviceNameActivity.class);
        intent.putExtra(Constant.GROUP_ID, groupId);
        intent.putExtra(Constant.DEVICE_PHONE_NUMBER,deviceNumber.getText().toString());
        intent.putExtra(Constant.DEVICE_IMEI_NUMBER,deviceImei.getText().toString());
        startActivity(intent);
    }
}
