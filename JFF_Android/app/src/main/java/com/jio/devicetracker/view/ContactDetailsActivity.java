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

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddMemberInGroupData;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.request.AddMemberInGroupRequest;
import com.jio.devicetracker.database.pojo.request.GetGroupMemberRequest;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for adding new devices(It can add from QR code, Contact list or manually)
 */
public class ContactDetailsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private String name;
    private String number;
    private EditText mName;
    private EditText mNumber;
    private DBManager mDbManager;
    private String deviceType;
    private boolean isDataMatched = false;
    private static String numberComingFromContactList = null;
    private static String nameComingFromContactList = null;
    private Button addContactInGroupButon = null;
    private Toolbar toolbar = null;
    private String phoneCountryCode;
    private String groupId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        toolbar = findViewById(R.id.loginToolbar);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.CONTACT_DEVICE_TITLE);
        addContactInGroupButon = findViewById(R.id.addContactInGroup);
        addContactInGroupButon.setOnClickListener(this);
        mName = findViewById(R.id.memberName);
        mNumber = findViewById(R.id.deviceNumberInContactDetails);
        Spinner deviceTypeSpinner = findViewById(R.id.deviceTypeSpinner);
        deviceTypeSpinner.setOnItemSelectedListener(this);
        mDbManager = new DBManager(this);
        phoneCountryCode = mDbManager.getAdminLoginDetail().getPhoneCountryCode();
        Intent intent = getIntent();
        String qrValue = intent.getStringExtra("QRCodeValue");
        groupId = intent.getStringExtra("groupId");
        userId = intent.getStringExtra("userId");
        setNameNumberImei(qrValue);
        changeButtonColorOnDataEntry();
        checkValidationOfFieldEntry();
    }

    /**
     * If coming to this activity after clicking on Add Contact or Create group from floating button, then display the contact icon on toolbar
     * or else display the QR code on toolbar when Add device is clicked
     */
    private void checkValidationOfFieldEntry() {
        if (DashboardActivity.isComingFromGroupList || DashboardActivity.isAddIndividual) {
            ImageView contactBtn = toolbar.findViewById(R.id.contactAdd);
            contactBtn.setVisibility(View.VISIBLE);
            contactBtn.setOnClickListener(this);
        } else {
            ImageView scanner = toolbar.findViewById(R.id.qrScanner);
            scanner.setVisibility(View.VISIBLE);
            scanner.setOnClickListener(this);
        }

        if ("".equals(numberComingFromContactList) && "".equals(nameComingFromContactList)) {
            mName.setText(nameComingFromContactList);
            mNumber.setText(numberComingFromContactList);
        }
    }

    /**
     * Change the button color when data is field
     */
    private void changeButtonColorOnDataEntry() {
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*addContactInGroupButon.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                addContactInGroupButon.setTextColor(Color.WHITE);*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = mName.getText().toString();
                if (!"".equalsIgnoreCase(name)) {
                    addContactInGroupButon.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                    addContactInGroupButon.setTextColor(Color.WHITE);
                }
            }
        });

    }

    /**
     * Fetch the name and number after scanning from QR Scanner
     *
     * @param qrValue
     */
    private void setNameNumberImei(String qrValue) {
        if (qrValue != null) {
            String[] splitString = qrValue.split("\n");
            name = splitString[0];
            number = splitString[1];
            mName.setText(name);
            mNumber.setText(number);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contactAdd) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, 1);
        } else if (v.getId() == R.id.addContactInGroup) {
            if (mName.getText().toString().equalsIgnoreCase("")) {
                mName.setError(Constant.NAME_EMPTY);
                return;
            }
            name = mName.getText().toString().trim();
            number = mNumber.getText().toString().trim();
            if (deviceType.equalsIgnoreCase(Constant.PEOPLE_TRACKER_DEVICE_TYPE)) {
                if (Util.isValidMobileNumberForPet(number)) {
                    mNumber.setError(Constant.PEOPLE_NUMBER_VALIDATION_PET_NUMBER_ENTERED);
                    return;
                } else if (!Util.isValidMobileNumber(number)) {
                    mNumber.setError(Constant.MOBILENUMBER_VALIDATION);
                    return;
                }
            } else if (deviceType.equalsIgnoreCase(Constant.PET_TRACKER_DEVICE_TYPE)) {
                if (Util.isValidMobileNumber(number)) {
                    mNumber.setError(Constant.PET_TRACKER_VALIDATION_PEOPLE_NUMBER_ENTERE);
                    return;
                } else if (!Util.isValidMobileNumberForPet(number)) {
                    mNumber.setError(Constant.PET_NUMBER_VALIDATION);
                    return;
                }
            }
            addMemberInGroupAPICall();
        }

        if (v.getId() == R.id.qrScanner) {
            gotoQRScannerScreen();
        }
    }

    /**
     * Add Members in Group API Call
     */
    public void addMemberInGroupAPICall() {
        AddMemberInGroupData addMemberInGroupData = new AddMemberInGroupData();
        AddMemberInGroupData.Consents consents = new AddMemberInGroupData().new Consents();
        List<AddMemberInGroupData.Consents> consentList = new ArrayList<>();
        List<String> mList = new ArrayList<>();
        mList.add("events");
        consents.setEntities(mList);
        consents.setPhone(number);
        consents.setName(name);
        consentList.add(consents);
        addMemberInGroupData.setConsents(consentList);
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(this).handleRequest(new AddMemberInGroupRequest(new AddMemberInGroupRequestSuccessListener(), new AddMemberInGroupRequestErrorListener(), addMemberInGroupData, groupId, userId));
    }

    /**
     * Add Member in group Success Listener
     */
    private class AddMemberInGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GroupMemberResponse groupMemberResponse = Util.getInstance().getPojoObject(String.valueOf(response), GroupMemberResponse.class);
            if (groupMemberResponse.getCode() == Constant.SUCCESS_CODE_200) {
                mDbManager.insertGroupMemberDataInTable(groupMemberResponse);
                getAllForOneGroupAPICall();
            }
        }
    }

    /**
     * Add Member in Group Error Listener
     */
    private class AddMemberInGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                Util.progressDialog.dismiss();
                Util.alertDilogBox(Constant.GROUP_MEMBER_ADDITION_FAILURE, Constant.ALERT_TITLE, ContactDetailsActivity.this);
            } else if (error.networkResponse.statusCode == Constant.STATUS_CODE_404) {
                // Make Verify and Assign call
                Util.progressDialog.dismiss();
                Util.alertDilogBox(Constant.DEVICE_NOT_FOUND, Constant.ALERT_TITLE, ContactDetailsActivity.this);
            }
        }
    }

    /**
     * Get all members of a particular group
     */
    public void getAllForOneGroupAPICall() {
        GroupRequestHandler.getInstance(this).handleRequest(new GetGroupMemberRequest(new GetGroupMemberRequestSuccessListener(), new GetGroupMemberRequestErrorListener(), groupId, userId));
    }

    /**
     * Get all members of a particular group success listener
     */
    public class GetGroupMemberRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            GroupMemberResponse groupMemberResponse = Util.getInstance().getPojoObject(String.valueOf(response), GroupMemberResponse.class);
            if (groupMemberResponse.getCode() == Constant.SUCCESS_CODE_200) {
                mDbManager.insertGroupMemberDataInTable(groupMemberResponse);
                gotoGroupListActivity();
            }
        }
    }

    /**
     * Get all members of a particular group error listener
     */
    private class GetGroupMemberRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
        }
    }

    /**
     * If data is matched then update device type and group name in database
     *
     * @param mName
     * @param mNumber
     * @param mIMEINumber
     */
    private void checkValidationOfUser(String mName, String mNumber, String mIMEINumber) {
        List<HomeActivityListData> homeListData = mDbManager.getAllBorqsData(Util.adminEmail);
        for (HomeActivityListData homeActivityListData : homeListData) {
            if (homeActivityListData.getImeiNumber() != null && homeActivityListData.getImeiNumber().equalsIgnoreCase(mIMEINumber) && homeActivityListData.getPhoneNumber().equalsIgnoreCase(mNumber)) {
                mDbManager.updateDeviceTypeAndGroupName(deviceType, homeActivityListData.getGroupName(), homeActivityListData.getImeiNumber(), mName, 1);
                isDataMatched = true;
            }
        }
        if (!isDataMatched) {
            Util.alertDilogBox(Constant.DEVICE_DETAIL_VALIDATION, Constant.ALERT_TITLE, this);
            return;
        }
    }

    /**
     * Call back method of QR scanner button available on toolbar
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                ContentResolver resolver = getContentResolver();
                Cursor cursor = resolver.query(contactData, null, null, null, null);
                cursor.moveToFirst();
                String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if ("1".equalsIgnoreCase(hasPhone)) {
                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, null);
                    phones.moveToFirst();
                    numberComingFromContactList = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    nameComingFromContactList = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                }
                startActivity(new Intent(this, ContactDetailsActivity.class));
            }
        }
    }

    private void gotoGroupListActivity() {
        Intent intent = new Intent(this, GroupListActivity.class);
        intent.putExtra("groupId", groupId);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void gotoDashboardActivity() {
        startActivity(new Intent(this, DashboardActivity.class));
    }

    private void gotoQRScannerScreen() {
        startActivity(new Intent(this, QRCodeReaderActivity.class));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        deviceType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //To-Do
    }

}
