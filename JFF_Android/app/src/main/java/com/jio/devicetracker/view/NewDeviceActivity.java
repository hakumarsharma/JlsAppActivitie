/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.

 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.

 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.view;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
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
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.request.AddDeviceRequest;
import com.jio.devicetracker.database.pojo.response.AddDeviceResponse;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of Add Device Screen for adding the tracee number.
 */
public class NewDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent = null;
    private String name;
    private String number;
    private String imeiNumber;
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
        title.setText(Constant.ADD_DEVICE_TITLE);
        intent = getIntent();
        String qrValue = intent.getStringExtra("QRCodeValue");
        adminData = new AdminLoginData();
        mDeviceNumber = findViewById(R.id.deviceName);
        mName = findViewById(R.id.memberName);
        mDeviceImeiNumber = findViewById(R.id.deviceIMEINumber);
        Button mAdd = findViewById(R.id.save);
        mDbManager = new DBManager(this);
        mAdd.setOnClickListener(this);
        setNameNumberImei(qrValue);
    }

    private void setNameNumberImei(String qrValue) {
        if(qrValue != null){
            String []splitString = qrValue.split("\n");
            name = splitString[0];
            number = splitString[1];
            imeiNumber = splitString[2];
            mName.setText(name);
            mDeviceNumber.setText(number);
            mDeviceImeiNumber.setText(imeiNumber);

        }

    }

    @Override
    public void onClick(View v) {
        String number = mDeviceNumber.getText().toString();
        String name = mName.getText().toString();
        String phoneNumber = mDbManager.getAdminphoneNumber();
        String imeiNumber = mDeviceImeiNumber.getText().toString();
        if (!Util.isValidIMEINumber(imeiNumber)) {
            mDeviceImeiNumber.setError(Constant.IMEI_VALIDATION);
            return;
        }

        if (!Util.isValidMobileNumber(number)) {
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
        devices.setIdentifier(Constant.IMEI);
        devices.setName(name);
        devices.setPhone(number);
        flags.setSkipAddDeviceToGroup(false);
        List<AddDeviceData.Devices> listDevices = new LinkedList<>();
        listDevices.add(devices);
        addDeviceData.setDevices(listDevices);
        addDeviceData.setFlags(flags);
        Util.getInstance().showProgressBarDialog(this, Constant.PROGRESSBAR_MSG);
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new AddDeviceRequest(new SuccessListener(), new ErrorListener(), new DBManager(this).getAdminLoginDetail().getUserToken(), new DBManager(this).getAdminLoginDetail().getUserId(), addDeviceData));
    }

    private class SuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            AddDeviceResponse addDeviceResponse = Util.getInstance().getPojoObject(String.valueOf(response), AddDeviceResponse.class);
            adminData = mDbManager.getAdminLoginDetail();
            if(addDeviceResponse.getMessage().equalsIgnoreCase(Constant.SUCCESSFULL_DEVICE_ADDITION_RESPONSE)){
                HomeActivityListData addedDeviceData = new HomeActivityListData();
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
    private void gotoDashBoard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra(Constant.NAME,mName.getText().toString());
        intent.putExtra(Constant.FLAG,true);
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
