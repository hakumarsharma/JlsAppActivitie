// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.response.TrackerdeviceResponse;
import com.jio.devicetracker.jiotoken.JiotokenHandler;
import com.jio.devicetracker.util.Util;

import java.util.List;
import java.util.regex.Pattern;

public class NewDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent = null;
    private long insertRowid;
    private List<TrackerdeviceResponse.Data> mDatalist;
    private EditText mDeviceNumber, mName, mDeviceImeiNumber;
    private Button mAdd;
    private String number, name, imeiNumber;
    private AddedDeviceData data = null;
    private DBManager mDbManager;

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
        mAdd = findViewById(R.id.save);
        mDbManager = new DBManager(this);
        mAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        data = new AddedDeviceData();
        number = mDeviceNumber.getText().toString();
        name = mName.getText().toString();
        imeiNumber = mDeviceImeiNumber.getText().toString();
        if (!isValidIMEINumber(imeiNumber)) {
            mDeviceImeiNumber.setError("Enter the 15 digit IMEI number");
            return;
        }

        if (number.equals("") || name.equals("") || imeiNumber.equals("")) {
            Toast.makeText(this, "Please Enter the details", Toast.LENGTH_SHORT).show();
        } else {
            data.setImeiNumber(imeiNumber);
            data.setPhoneNumber(number);
            data.setRelation(name);
            matchMobileNumber(data);
        }
    }

    private boolean isValidIMEINumber(String imei) {
        String imeiNumber = "^\\d{15}$";
        Pattern pat = Pattern.compile(imeiNumber);
        return pat.matcher(imei).matches();
    }

    private void matchMobileNumber(AddedDeviceData adddeviceData) {
        boolean isLatLngFound = false;
        if(JiotokenHandler.ssoToken == null) {
            for (int i = 0; i < mDatalist.size(); i++) {
                String phoneNumber = adddeviceData.getPhoneNumber().trim();
                if (phoneNumber.equals(mDatalist.get(i).getmDevice().getPhoneNumber())) {
                    if (mDatalist.get(i).getEvent() != null) {
                        isLatLngFound = true;
                        adddeviceData.setLat(mDatalist.get(i).getEvent().getLocation().getLatLocation().getLatitu());
                        adddeviceData.setLng(mDatalist.get(i).getEvent().getLocation().getLatLocation().getLongni());
                        insertRowid = mDbManager.insertInBorqsDB(adddeviceData);
                        //gotoDashBoard();
                        checkRow(insertRowid);
                        break;
                    } else if (mDatalist.get(i).getLocation() != null) {
                        isLatLngFound = true;
                        adddeviceData.setLat(mDatalist.get(i).getLocation().getLat().toString().trim());
                        adddeviceData.setLng(mDatalist.get(i).getLocation().getLng().toString().trim());
                        insertRowid = mDbManager.insertInBorqsDB(adddeviceData);
                        checkRow(insertRowid);
                        //gotoDashBoard();
                        break;
                    }
                }
            }
        }
        else {
            insertRowid = mDbManager.insertInFMSDB(adddeviceData);
            checkRow(insertRowid);
        }
        if (!isLatLngFound && JiotokenHandler.ssoToken == null) {
            mDeviceNumber.setError("Enter the correct number, this number is not found on server");
            return;
        }
    }

    private void gotoDashBoard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    private void checkRow(long id)
    {
        if(id == -1)
        {
            Util.alertDilogBox("This number is already added","Jio Alert",this);
        } else {
            gotoDashBoard();
        }
    }
}
