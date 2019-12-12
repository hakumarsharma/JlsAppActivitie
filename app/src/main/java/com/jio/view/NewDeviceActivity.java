// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jio.database.pojo.AddedDeviceData;
import com.jio.database.pojo.response.TrackerdeviceResponse;

import java.io.Serializable;
import java.util.List;

public class NewDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent = null;
    private List<TrackerdeviceResponse.Data> mDatalist;
    private EditText mDeviceNumber, mName, mDeviceImeiNumber ,mLocation;
    private Button mAdd;
    private String number, name, imeiNumber;
    private AddedDeviceData data = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);

        TextView title = findViewById(R.id.toolbar_title);
        title.setText("Add");
        intent = getIntent();
        mDatalist = (List<TrackerdeviceResponse.Data>) intent.getSerializableExtra("DeviceData");
        mDeviceNumber = findViewById(R.id.deviceName);
        mName = findViewById(R.id.memberName);
        mDeviceImeiNumber = findViewById(R.id.deviceIMEINumber);
        mLocation = findViewById(R.id.deviceLocation);
        mAdd = findViewById(R.id.save);
        mAdd.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        data = new AddedDeviceData();
        number = mDeviceNumber.getText().toString();
        name = mName.getText().toString();
        imeiNumber = mDeviceImeiNumber.getText().toString();

        if (number.equals("") || name.equals("") || imeiNumber.equals("")) {
            Toast.makeText(this, "Please Enter the details", Toast.LENGTH_SHORT).show();
        } else {
            data.setImeiNumber(imeiNumber);
            data.setPhoneNumber(number);
            data.setRelation(name);
            matchMobileNumber(data);
        }

    }

    private void matchMobileNumber(AddedDeviceData adddeviceData) {
        boolean isLatLngFound = false;
        for (int i = 0; i < mDatalist.size(); i++) {
            String phoneNumber = adddeviceData.getPhoneNumber().toString().trim();
            if (phoneNumber.equals(mDatalist.get(i).getmDevice().getPhoneNumber().toString())) {
                if (mDatalist.get(i).getEvent() != null) {
                    isLatLngFound = true;
                    adddeviceData.setLat(mDatalist.get(i).getEvent().getLocation().getLatLocation().getLatitu().toString());
                    adddeviceData.setLng(mDatalist.get(i).getEvent().getLocation().getLatLocation().getLongni().toString());
                    gotoDashBoard(adddeviceData);
                    break;
                }
                else if(mDatalist.get(i).getLocation() != null) {
                    isLatLngFound = true;
                    adddeviceData.setLat(mDatalist.get(i).getLocation().getLat().toString().trim());
                    adddeviceData.setLng(mDatalist.get(i).getLocation().getLng().toString().trim());
                    gotoDashBoard(adddeviceData);
                    break;
                }
            }
        }
        if(!isLatLngFound) {
            Toast.makeText(getApplicationContext(), "Lat and Long is not updated on server", Toast.LENGTH_SHORT).show();
        }
    }

    private void gotoDashBoard(AddedDeviceData adddeviceData) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("AddDeviceData", adddeviceData);
        startActivity(intent);
    }
}
