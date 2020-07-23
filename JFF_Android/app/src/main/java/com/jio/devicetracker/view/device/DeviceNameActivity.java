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

package com.jio.devicetracker.view.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddDeviceData;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.DeviceTableData;
import com.jio.devicetracker.database.pojo.SearchDeviceStatusData;
import com.jio.devicetracker.database.pojo.request.AddDeviceRequest;
import com.jio.devicetracker.database.pojo.request.GetUserDevicesListRequest;
import com.jio.devicetracker.database.pojo.response.AddDeviceResponse;
import com.jio.devicetracker.database.pojo.response.GetUserDevicesListResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.BaseActivity;
import com.jio.devicetracker.view.dashboard.DashboardMainActivity;
import com.jio.devicetracker.view.group.ChooseGroupActivity;

import java.util.ArrayList;
import java.util.List;

public class DeviceNameActivity extends BaseActivity implements View.OnClickListener {

    private ImageView motherIcon;
    private ImageView fatherIcon;
    private ImageView husbandIcon;
    private ImageView wifeIcon;
    private ImageView kidIcon;
    private ImageView otherIcon;
    private ImageView dogIcon;
    private ImageView catIcon;
    private ImageView otherPetIcon;
    private EditText deviceName;
    private Button done;
    private String deviceNumber;
    private String deviceImei;
    public static String groupId;
    private DBManager mDbManager;
    public String selectedIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_name);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.DEVICE_NAME_TITLE);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        done = findViewById(R.id.done);
        done.setVisibility(View.VISIBLE);
        done.setOnClickListener(this);
        done.setAlpha(.5f);

        mDbManager = new DBManager(this);
        selectedIcon = Constant.OTHER;

        Intent intent = getIntent();
        deviceNumber = intent.getStringExtra(Constant.DEVICE_PHONE_NUMBER);
        deviceImei = intent.getStringExtra(Constant.DEVICE_IMEI_NUMBER);

        groupId = intent.getStringExtra(Constant.GROUP_ID);
        title.setTypeface(Util.mTypeface(this, 5));
        TextView iconSelectionText = findViewById(R.id.icon_selection);
        iconSelectionText.setTypeface(Util.mTypeface(this, 3));
        // createdGroupIdFromPeople = this.createdGroupId;
        initiateUI();
    }

    public void initiateUI() {
        motherIcon = findViewById(R.id.mother_icon);
        fatherIcon = findViewById(R.id.father_icon);
        husbandIcon = findViewById(R.id.husband_icon);
        wifeIcon = findViewById(R.id.wife_icon);
        kidIcon = findViewById(R.id.kid_icon);
        otherIcon = findViewById(R.id.other_icon);
        dogIcon = findViewById(R.id.dog_icon);
        catIcon = findViewById(R.id.cat_icon);
        otherPetIcon = findViewById(R.id.other_pet_icon);
        deviceName = findViewById(R.id.device_edit_name);
        motherIcon.setOnClickListener(this);
        fatherIcon.setOnClickListener(this);
        wifeIcon.setOnClickListener(this);
        husbandIcon.setOnClickListener(this);
        kidIcon.setOnClickListener(this);
        otherIcon.setOnClickListener(this);
        dogIcon.setOnClickListener(this);
        catIcon.setOnClickListener(this);
        otherPetIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String name = deviceName.getText().toString().trim();
        switch (v.getId()) {
            case R.id.mother_icon:
                if (motherIcon.getTag() != null && motherIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.MOM)) {
                    motherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mother, null));
                    motherIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                    selectedIcon = Constant.OTHER;
                } else {
                    motherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    motherIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.MOM);
                    selectedIcon = Constant.MOM;
                }
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                done.setAlpha(1);
                break;
            case R.id.father_icon:
                if (fatherIcon.getTag() != null && fatherIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.FATHER)) {
                    fatherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.father, null));
                    fatherIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                    selectedIcon = Constant.OTHER;
                } else {
                    fatherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    fatherIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.FATHER);
                    selectedIcon = Constant.FATHER;
                }
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                done.setAlpha(1);
                break;
            case R.id.husband_icon:
                if (husbandIcon.getTag() != null && husbandIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.HUSBAND)) {
                    husbandIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.husband, null));
                    husbandIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                    selectedIcon = Constant.OTHER;
                } else {
                    husbandIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    husbandIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.HUSBAND);
                    selectedIcon = Constant.HUSBAND;
                }
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                done.setAlpha(1);
                break;
            case R.id.wife_icon:
                if (wifeIcon.getTag() != null && wifeIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.WIFE)) {
                    wifeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wife, null));
                    wifeIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                    selectedIcon = Constant.OTHER;
                } else {
                    wifeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    wifeIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.WIFE);
                    selectedIcon = Constant.WIFE;
                }
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                kidIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                done.setAlpha(1);
                break;
            case R.id.kid_icon:
                if (kidIcon.getTag() != null && kidIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.KID)) {
                    kidIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.kid, null));
                    kidIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                    selectedIcon = Constant.OTHER;
                } else {
                    kidIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    kidIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.KID);
                    selectedIcon = Constant.KID;
                }
                motherIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
                fatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
                husbandIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
                wifeIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                otherIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
                dogIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
                catIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
                otherPetIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
                done.setAlpha(1);
                break;
            case R.id.other_icon:
                if (otherIcon.getTag() != null && otherIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.OTHER)) {
                    otherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other, null));
                    otherIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                    selectedIcon = Constant.OTHER;
                } else {
                    otherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    otherIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.OTHER);
                    selectedIcon = Constant.OTHER;
                }
                motherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mother, null));
                fatherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.father, null));
                husbandIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.husband, null));
                wifeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wife, null));
                kidIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.kid, null));
                dogIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dog, null));
                catIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cat, null));
                otherPetIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other_pet, null));
                done.setAlpha(1);
                break;
            case R.id.dog_icon:
                if (dogIcon.getTag() != null && dogIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.DOG)) {
                    dogIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dog, null));
                    dogIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                    selectedIcon = Constant.OTHER;
                } else {
                    dogIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    dogIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.DOG);
                    selectedIcon = Constant.DOG;
                }
                motherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mother, null));
                fatherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.father, null));
                husbandIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.husband, null));
                wifeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wife, null));
                kidIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.kid, null));
                otherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other, null));
                catIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cat, null));
                otherPetIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other_pet, null));
                done.setAlpha(1);
                break;
            case R.id.cat_icon:
                if (catIcon.getTag() != null && catIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.CAT)) {
                    catIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cat, null));
                    catIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                    selectedIcon = Constant.OTHER;
                } else {
                    catIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    catIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.CAT);
                    selectedIcon = Constant.CAT;
                }
                motherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mother, null));
                fatherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.father, null));
                husbandIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.husband, null));
                wifeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wife, null));
                kidIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.kid, null));
                otherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other, null));
                dogIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dog, null));
                otherPetIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other_pet, null));
                done.setAlpha(1);
                break;
            case R.id.other_pet_icon:
                if (otherPetIcon.getTag() != null && otherPetIcon.getTag().equals(Constant.GROUP_ICON)
                        && name.equalsIgnoreCase(Constant.EMPTY_STRING) || name.equalsIgnoreCase(Constant.OTHER_PET)) {
                    otherPetIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other_pet, null));
                    otherPetIcon.setTag(Constant.REAL_ICON);
                    deviceName.setText(Constant.EMPTY_STRING);
                    selectedIcon = Constant.OTHER;
                } else {
                    otherPetIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.groupselected, null));
                    otherPetIcon.setTag(Constant.GROUP_ICON);
                    deviceName.setText(Constant.OTHER_PET);
                    selectedIcon = Constant.OTHER_PET;
                }
                motherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mother, null));
                fatherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.father, null));
                husbandIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.husband, null));
                wifeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wife, null));
                kidIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.kid, null));
                otherIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.other, null));
                dogIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dog, null));
                catIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cat, null));
                done.setAlpha(1);
                break;

            case R.id.done:
                if (deviceName.getText().length() == 0) {
                    deviceName.setError(Constant.ENTER_DEVICE_NAME);
                    return;
                } else if (DashboardMainActivity.flowFromGroup) {
                    getUserDevicesList();
                } else {
                    gotoChooseGroupActivity(selectedIcon);
                }
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;

        }
    }

    private void addMemberToCreatedGroup() {
        this.createdGroupId = groupId;
        this.memberName = deviceName.getText().toString();
        this.memberNumber = deviceNumber;
        this.isFromCreateGroup = true;
        this.isGroupMember = false;
        this.isFromDevice = true;
        Util.progressDialog.dismiss();
        addMemberInGroupAPICall();
    }

    public void gotoChooseGroupActivity(String iconName) {
        Intent intent = new Intent(this, ChooseGroupActivity.class);
        intent.putExtra(Constant.GROUP_ID, createdGroupId);
        intent.putExtra(Constant.DEVICE_PHONE_NUMBER, deviceNumber);
        intent.putExtra(Constant.DEVICE_IMEI_NUMBER, deviceImei);
        intent.putExtra(Constant.REAL_ICON, iconName);
        intent.putExtra(Constant.TITLE_NAME, deviceName.getText().toString());
        startActivity(intent);
    }

    /**
     * Get User Devices list API call
     */
    private void getUserDevicesList() {
        AdminLoginData adminLoginDetail = mDbManager.getAdminLoginDetail();
        List<String> data = new ArrayList<>();
        if (adminLoginDetail != null) {
            data.add(adminLoginDetail.getUserId());
            SearchDeviceStatusData searchDeviceStatusData = new SearchDeviceStatusData();
            SearchDeviceStatusData.Device device = searchDeviceStatusData.new Device();
            device.setUsersAssigned(data);
            searchDeviceStatusData.setDevice(device);
            Util.getInstance().showProgressBarDialog(this);
            GroupRequestHandler.getInstance(getApplicationContext()).handleRequest(new GetUserDevicesListRequest(new GetDeviceRequestSuccessListener(), new GetDeviceRequestErrorListener(), searchDeviceStatusData));
        }
    }

    /**
     * Get Devices list API call success listener
     */
    private class GetDeviceRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GetUserDevicesListResponse getDeviceResponse = Util.getInstance().getPojoObject(String.valueOf(response), GetUserDevicesListResponse.class);
            if (getDeviceResponse.getCode() == 200) {
                boolean isNumberExists = false;
                for (GetUserDevicesListResponse.Data data : getDeviceResponse.getData()) {
                    if (data.getDevices().getImei().equalsIgnoreCase(deviceImei)){
                        isNumberExists = true;
                        break;
                    }
                }

                if (isNumberExists) {
                    addMemberToCreatedGroup();
                } else {
                    makeVerifyAndAssignAPICall();
                }
            }
        }
    }

    /**
     * Get Devices list API call error listener
     */
    private class GetDeviceRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Toast.makeText(DeviceNameActivity.this, Constant.UNSUCCESSFULL_DEVICE_ADD, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Verify and assign API Call for the white-listing of device
     */
    private void makeVerifyAndAssignAPICall() {
        AddDeviceData addDeviceData = new AddDeviceData();
        List<AddDeviceData.Devices> mList = new ArrayList<>();
        AddDeviceData.Devices devices = new AddDeviceData().new Devices();
        devices.setMac(deviceImei);
        devices.setPhone(deviceNumber);
        devices.setIdentifier("imei");
        devices.setName(deviceName.getText().toString());
        devices.setType("watch");
        devices.setModel("watch");
        AddDeviceData.Devices.Metaprofile metaprofile = new AddDeviceData().new Devices().new Metaprofile();
        metaprofile.setFirst(deviceName.getText().toString());
        metaprofile.setSecond("success");
        devices.setMetaprofile(metaprofile);
        AddDeviceData.Flags flags = new AddDeviceData().new Flags();
        flags.setSkipAddDeviceToGroup(false);
        addDeviceData.setFlags(flags);
        mList.add(devices);
        addDeviceData.setDevices(mList);
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new AddDeviceRequest(new AddDeviceRequestSuccessListener(), new AddDeviceRequestErrorListener(), mDbManager.getAdminLoginDetail().getUserToken(), mDbManager.getAdminLoginDetail().getUserId(), addDeviceData));
    }

    /**
     * Verify & Assign API call success listener
     */
    private class AddDeviceRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            AddDeviceResponse addDeviceResponse = Util.getInstance().getPojoObject(String.valueOf(response), AddDeviceResponse.class);
            if (addDeviceResponse.getCode() == 200) {
                DeviceTableData deviceTableData = mDbManager.getDeviceTableData(deviceImei);
                if (deviceTableData == null) {
                    DeviceTableData mDeviceTableData = new DeviceTableData();
                    mDeviceTableData.setPhoneNumber(deviceNumber);
                    mDeviceTableData.setImeiNumber(deviceImei);
                    mDeviceTableData.setAdditionCount(0);
                    mDbManager.insertIntoDeviceTable(mDeviceTableData);
                } else {
                    int count = deviceTableData.getAdditionCount();
                    DeviceTableData mDeviceTableData = new DeviceTableData();
                    mDeviceTableData.setAdditionCount(++count);
                    mDbManager.updateIntoDeviceTable(mDeviceTableData);
                }
                Util.progressDialog.dismiss();
                addMemberToCreatedGroup();
                //Toast.makeText(ChooseGroupActivity.this, Constant.SUCCESSFULL_DEVICE_ADDITION, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Verify & Assign API call error listener
     */
    private class AddDeviceRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Toast.makeText(DeviceNameActivity.this, Constant.UNSUCCESSFULL_DEVICE_ADDITION, Toast.LENGTH_SHORT).show();
        }
    }
}
