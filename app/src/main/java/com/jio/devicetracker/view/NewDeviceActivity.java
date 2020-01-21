// (c) Copyright 2019 by Reliance JIO. All rights reserved.
package com.jio.devicetracker.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddDeviceData;
import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.request.AddDeviceRequest;
import com.jio.devicetracker.database.pojo.request.TrackdeviceRequest;
import com.jio.devicetracker.database.pojo.response.AddDeviceResponse;
import com.jio.devicetracker.database.pojo.response.TrackerdeviceResponse;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class NewDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent = null;
    private EditText mDeviceNumber;
    private EditText mName;
    private EditText mDeviceImeiNumber;
    private DBManager mDbManager;
    private AdminLoginData adminData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText("Add");
        intent = getIntent();
        adminData = new AdminLoginData();
        mDeviceNumber = findViewById(R.id.deviceName);
        mName = findViewById(R.id.memberName);
        mDeviceImeiNumber = findViewById(R.id.deviceIMEINumber);
        Button mAdd = findViewById(R.id.save);
        mDbManager = new DBManager(this);
        mAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String number = mDeviceNumber.getText().toString();
        String name = mName.getText().toString();
        String phoneNumber = mDbManager.getAdminphoneNumber();
        String imeiNumber = mDeviceImeiNumber.getText().toString();
        if (!isValidIMEINumber(imeiNumber)) {
            mDeviceImeiNumber.setError(Constant.IMEI_VALIDATION);
            return;
        }

        if (!isValidMobileNumber(number)) {
            mDeviceNumber.setError(Constant.MOBILENUMBER_VALIDATION);
            return;
        }
        if(phoneNumber.equals("91"+ number)) {
            Util.alertDilogBox(Constant.REGMOBILENUMBER_VALIDATION,Constant.ALERT_TITLE,this);
            return;
        }

        if (number.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, Constant.CHECK_DETAILS, Toast.LENGTH_SHORT).show();
        } else {
            makeAddDeviceAPICall();
//          matchMobileNumber(data);
        }
    }

    private void makeAddDeviceAPICall(){
        String number = mDeviceNumber.getText().toString();
        String name = mName.getText().toString();
        String imeiNumber = mDeviceImeiNumber.getText().toString();
        AddDeviceData addDeviceData = new AddDeviceData();
        AddDeviceData.Devices devices = addDeviceData.new Devices();
        AddDeviceData.Flags flags = addDeviceData.new Flags();
        devices.setMac(imeiNumber);
        devices.setIdentifier("imei");
        devices.setName(name);
        devices.setPhone(number);
        flags.setSkipAddDeviceToGroup(false);
        List<AddDeviceData.Devices> listDevices = new LinkedList<>();
        listDevices.add(devices);
        addDeviceData.setDevices(listDevices);
        addDeviceData.setFlags(flags);
        Util.getInstance().showProgressBarDialog(this, "Please wait adding device");
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new AddDeviceRequest(new SuccessListener(), new ErrorListener(), new DBManager(this).getAdminLoginDetail().getUserToken(), new DBManager(this).getAdminLoginDetail().getUserId(), addDeviceData));
    }

    private class SuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            AddDeviceResponse addDeviceResponse = Util.getInstance().getPojoObject(String.valueOf(response), AddDeviceResponse.class);
            adminData = mDbManager.getAdminLoginDetail();
            if(addDeviceResponse.getMessage().equalsIgnoreCase(Constant.SUCCESSFULL_DEVICE_ADDITION_RESPONSE)){
                AddedDeviceData addedDeviceData = new AddedDeviceData();
                addedDeviceData.setName(addDeviceResponse.getData().getAssignedDevices().get(0).getName());
                addedDeviceData.setPhoneNumber(addDeviceResponse.getData().getAssignedDevices().get(0).getPhone());
                addedDeviceData.setImeiNumber(addDeviceResponse.getData().getAssignedDevices().get(0).getMac());
                long insertRowid = mDbManager.insertInBorqsDB(addedDeviceData, adminData.getEmail());
                Toast.makeText(NewDeviceActivity.this, Constant.SUCCESSFULL_DEVICE_ADDITION, Toast.LENGTH_SHORT).show();
                checkRow(insertRowid);
            }
            Util.getInstance().dismissProgressBarDialog();
        }
    }

    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.getInstance().dismissProgressBarDialog();
            Toast.makeText(NewDeviceActivity.this, Constant.UNSUCCESSFULL_DEVICE_ADDITION, Toast.LENGTH_SHORT).show();
        }
    }

    /*private void matchMobileNumber(AddedDeviceData addedDeviceData) {
        adminData = mDbManager.getAdminLoginDetail();
        boolean isLatLngFound = false;
        long insertRowid;
        if(!RegistrationActivity.isFMSFlow && mDatalist != null) {
            for (TrackerdeviceResponse.Data data : mDatalist) {
                String phoneNumber = addedDeviceData.getPhoneNumber().trim();
                if (phoneNumber.equals(data.getmDevice().getPhoneNumber())) {
                    if (data.getEvent() != null) {
                        isLatLngFound = true;
                        addedDeviceData.setLat(data.getEvent().getLocation().getLatLocation().getLatitu());
                        addedDeviceData.setLng(data.getEvent().getLocation().getLatLocation().getLongni());
                        insertRowid = mDbManager.insertInBorqsDB(addedDeviceData,adminData.getEmail());
                        //gotoDashBoard();
                        checkRow(insertRowid);
                        break;
                    } else if (data.getLocation() != null) {
                        isLatLngFound = true;
                        addedDeviceData.setLat(data.getLocation().getLat().toString().trim());
                        addedDeviceData.setLng(data.getLocation().getLng().toString().trim());
                        insertRowid = mDbManager.insertInBorqsDB(addedDeviceData,adminData.getEmail());
                        checkRow(insertRowid);
                        //gotoDashBoard();
                        break;
                    }
                }
            }
        }
        else {
            insertRowid = mDbManager.insertInFMSDB(addedDeviceData);
            checkRow(insertRowid);
        }
        if (!isLatLngFound) {
            mDeviceNumber.setError("Enter the correct number, this number is not found on server");
            return;
        }
    }*/

    // TODO move to Utils
    private boolean isValidIMEINumber(String imei) {
        String imeiNumber = "^\\d{15}$";
        Pattern pat = Pattern.compile(imeiNumber);
        return pat.matcher(imei).matches();
    }

    // TODO move to Utils
    private boolean isValidMobileNumber(String mobile) {
        String mobileNumber = "^[6-9][0-9]{9}$";
        Pattern pat = Pattern.compile(mobileNumber);
        return pat.matcher(mobile).matches();
    }

    private void gotoDashBoard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("phone",mDeviceNumber.getText().toString());
        intent.putExtra("flag",true);
        startActivity(intent);
    }

    private void checkRow(long id) {
        if(id == -1) {
            Util.alertDilogBox(Constant.DUPLICATE_NUMBER,Constant.ALERT_TITLE,this);
        } else {
            gotoDashBoard();
        }
    }
}
